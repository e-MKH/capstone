from sudachipy import dictionary, tokenizer
import re
import sys, io

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

tokenizer_obj = dictionary.Dictionary(dict="core").create()
split_mode = tokenizer.Tokenizer.SplitMode.C

def is_kanji(char):
    return '\u4e00' <= char <= '\u9fff'

def is_particle(pos): 
    return pos == "助詞"

def is_verb(pos): 
    return pos == "動詞"

def is_wago(word, pos):
    return not any(is_kanji(c) for c in word) and pos in ["名詞", "動詞", "形容詞"]

def analyze_japanese_difficulty(text):
    sentences = [s.strip() for s in re.split("[。！？]", text) if len(s.strip()) >= 15]
    total_words = kanji_chars = total_chars = wago_words = verb_count = particle_count = 0

    for sentence in sentences:
        tokens = tokenizer_obj.tokenize(sentence, split_mode)
        for m in tokens:
            word = m.surface()
            pos = m.part_of_speech()[0]
            total_words += 1
            total_chars += len(word)
            kanji_chars += sum(1 for c in word if is_kanji(c))
            if is_wago(word, pos):
                wago_words += 1
            if is_verb(pos):
                verb_count += 1
            if is_particle(pos):
                particle_count += 1

    if total_words == 0 or len(sentences) == 0:
        return {"score": 0, "level": "하"}

    avg_sentence_length = total_chars / len(sentences)
    kanji_ratio = kanji_chars / total_chars
    wago_ratio = wago_words / total_words
    verb_ratio = verb_count / total_words
    particle_ratio = particle_count / total_words


    # 수정된 코드
    score = (
        -0.056 * avg_sentence_length +
        -0.126 * kanji_ratio +
        -0.042 * wago_ratio +
        -0.145 * verb_ratio +
        -0.044 * particle_ratio +
        11.724
    )

    score = round(score, 2)

    if score >= 9.5:
        level  = "하"
    elif score >= 8.0:
        level = "중"
    else:
        level = "상"

    return {
        "score": score,
        "level": level,
        "sentence_len": round(avg_sentence_length, 2),
        "kanji_ratio": round(kanji_ratio, 2),
        "wago_ratio": round(wago_ratio, 2),
        "verb_ratio": round(verb_ratio, 2),
        "particle_ratio": round(particle_ratio, 2),
        "analyzed_sentences": len(sentences)
    }

if __name__ == "__main__":
    text = """
     思い通りに体が動かなくなるパーキンソン病のリハビリとして近年、卓球が注目されている。無理なく運動することで、症状の進行を遅らせる効果が期待できるといい、日本卓球協会は４月、神戸市で患者とオリンピアンらによる交歓会を開催。参加者からは「励みになる」との声が上がった。
    交歓会で卓球を楽しむ参加者たち
    「朝、起き出すのに１０分、１５分かかる。体も気持ちもきついけど、孤立するのが良くない。卓球は体幹にいいし、一人じゃないと思える」。交歓会に参加した山田哲郎さん（７１）は笑みを浮かべた。十数年前に発症し、リハビリを兼ねて６年ほど前に卓球を始めた。
    国際卓球連盟が「卓球の日」と定める４月２３日に行われ、今回で３回目。各地から１７人の患者が集まり、ソウル五輪代表の今井清美さん（５７）らとのラリーや試合を楽しんだ。本来は反則の「卓球台に手をつく」といった行為も認められ、今井さんも「垣根を越えて楽しめるのが卓球の魅力」と語る。
    パーキンソン病は運動の調整に関わる脳内物質ドーパミンが減り、手が震えたり、歩行が困難になったりする難病で、国内患者数は推計２９万人とされる。根本的な治療法はないが、卓球が運動療法の一つとして広まっており、国際卓球連盟は２０１９年から、患者対象の世界選手権を開く。
    昨年１０月にフランスであった世界選手権。福岡県から参加した小山理恵さん（６３）は女子シングルス準優勝、ダブルス優勝を果たした。発症８年目。主治医から卓球を勧められたといい、「卓球をしている間は楽しさばかり。おかげで前向きになれている」と語る。
    日本卓球協会スポーツ医・科学委員会は、パーキンソン病など神経疾患の症状緩和に関する研究を進める。担当の順天堂大医学部付属順天堂医院・星野泰延医師は「卓球に限らず、運動がいいというのは分かってきた。卓球は負荷が少ないのも利点」と話している。（平野和彦）
    """
    result = analyze_japanese_difficulty(text)
    print(result)
