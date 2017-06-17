package io.github.weiweiwang.time;

import com.fulmicoton.multiregexp.MultiPattern;
import com.fulmicoton.multiregexp.MultiPatternSearcher;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by wangweiwei01 on 17/4/16.
 */
public class TimeRecognitionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeRecognitionTest.class);

    @Test
    public void testTimeEntityRecognition() throws IOException {
        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();
        String[] texts = {"六月三号", "六月三日", "5月18日", "5月18号", "大前天", "大大前天", "上上周日", "六月十五日", "1972年", "80年", "今天", "去年", "1997年", "今晚", "今年", "最近两三年", "Hi，all.下午三点开会",
                "周一开会", "早上六点起床", "下下周一开会"};
        TimeNormalizer normalizer = new TimeNormalizer();
        for (String txt : texts) {
            TimeUnit[] unit = normalizer.parse(txt);//对于上/下的识别
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(txt);
            LOGGER.debug("text:{},time unit:{}, time entities:{}", txt, Arrays.asList(unit), timeEntityList);
            Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
        }
    }

    @Test
    public void testTimeRecognition() throws URISyntaxException, IOException {

        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();

        TimeNormalizer normalizer = new TimeNormalizer();

        InputStream in = TimeRecognitionTest.class.getResourceAsStream("/time_test_fixture.txt");
        LineIterator lineIterator = IOUtils.lineIterator(new BufferedReader(new InputStreamReader(in)));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");

        while (lineIterator.hasNext()) {
            String line = StringUtils.stripToNull(lineIterator.nextLine());
            if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                continue;
            }
            String[] splits = line.split("\\s");
            String text = splits[0];
            String expected = splits.length > 1 ? splits[1] : null;
            TimeUnit[] unit = normalizer.parse(text);//对于上/下的识别
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(text);
            LOGGER.debug("text:{},time unit:{}, time entities:{}", line, Arrays.asList(unit), timeEntityList);
            LOGGER.debug("====\t{}\t{}\t{}", text, simpleDateFormat.format(timeEntityList.get(0).getValue()),
                    simpleDateFormat.format(unit[0].getTime()));
            String unitFormat = simpleDateFormat.format(unit[0].getTime());
            String entityFormat = simpleDateFormat.format(timeEntityList.get(0).getValue());
            if (!(entityFormat.equals(expected) || unitFormat.equals(entityFormat))) {
                Assert.assertTrue(text + "=>" + timeEntityList.get(0), false);
            }
        }


//        String[] texts = {"上上周日", "六月三号", "六月三日", "6月9号下午3点到6月10号下午2点", "2年后", "3年前", "6年前", "一年后", "约占流动人口总数的百分之七十一点八",
//                "据新华社联合国6月22日电（记者谢美华）联合国秘书长安南22日在",
//                "上上周日", "六月十五日", "1972年",
//                "80年", "今天", "去年", "1997年", "今晚", "今年",
//                "最近两三年"};
//        for (String txt : texts) {
//            TimeUnit[] unit = normalizer.parse(txt);//对于上/下的识别
//            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(txt);
//            LOGGER.debug("text:{},time unit:{}, time entities:{}", txt, Arrays.asList(unit), timeEntityList);
//            Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//        }

        String text = "我的手机号是+8613683550315";
        List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(text);
        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);


//        text = "Hi，all.下午三点开会";
//        TimeUnit[] unit = normalizer.parse(text);// 抽取时间
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//        text = "周一开会";
//        unit = normalizer.parse(text);// 抽取时间
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//        text = "Hi，all.下周一下午三点开会";
//        unit = normalizer.parse(text);// 抽取时间
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//
//        text = "早上六点起床";
//        unit = normalizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//
//        text = "周一开会";
//        unit = normalizer.parse(text);// 如果本周已经是周二，识别为下周周一。同理处理各级时间。（未来倾向）
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//
//        text = "下下周一开会";
//        unit = normalizer.parse(text);//对于上/下的识别
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
//
//        text = "今天";
//        unit = normalizer.parse(text);//对于上/下的识别
//        LOGGER.debug("text:{},time entities:{}", text, Arrays.asList(unit));
//        timeEntityList = timeEntityRecognizer.parse(text);
//        LOGGER.debug("text:{},time entities:{}", text, timeEntityList);
//        Assert.assertEquals(unit[0].getTime(), timeEntityList.get(0).getValue());
    }


//    @Test
    public void testMultiPattern() throws IOException {
        InputStream in = TimeRecognitionTest.class.getResourceAsStream("/time.regex");
        List<String> regexList = IOUtils.readLines(in, "UTF-8").stream().map(StringUtils::stripToNull)
                .filter(item -> StringUtils.isNotEmpty(item) && !item.startsWith("#")).distinct()
//                .sorted((o1, o2) -> o2.length() - o1.length())
                .collect(Collectors.toList());
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("input regex[size={}, text={}]", regexList.size(), regexList);
        }
        long start = System.currentTimeMillis();
        MultiPatternSearcher searcher = MultiPattern.of(regexList).searcher();
        long end = System.currentTimeMillis();
        LOGGER.info("multi pattern searcher initialized for {} patterns, time used(s):{}", regexList.size(),
                (end - start) / 1000.0);

        String text = "5月18";
        MultiPatternSearcher.Cursor cursor = searcher.search(text);
        while (cursor.next()) {
            int pattern = cursor.match();
            int matchStart = cursor.start();
            int matchEnd = cursor.end();
            String matched = text.substring(matchStart, matchEnd);
            LOGGER.info("pattern:{}, start:{}, end:{}, matched:{}", pattern, start, end, matched);
        }
    }

    @Test
    public void testRegex() {
        Pattern p = Pattern.compile("([\\d一二三四五六七八九十]+)月([\\d一二三四五六七八九十]+)号");
        Matcher m = p.matcher("六月三号");
        boolean find = m.find();
        LOGGER.debug(find + "," + m.group());
        Assert.assertTrue(find);

        p = Pattern.compile("((上|这|本|下)+(周|星期)([一二三四五六七天日]|[1-7])?)");

        m = p.matcher("上上周日");
        find = m.find();
        LOGGER.debug(find + "," + m.group());
        Assert.assertTrue(find);

        p = Pattern.compile("[\\d一二三四五六七八九十]+月[\\d一二三四五六七八九十]+");
        m = p.matcher("5月18");
        find = m.find();
        LOGGER.debug(find + "," + m.group());
        Assert.assertTrue(find);
    }

    @Test
    public void testTimeRecognitionWithCorpus() throws IOException {
        List<String> corpus = getTestCorpus();
        TimeEntityRecognizer timeEntityRecognizer = new TimeEntityRecognizer();

        TimeNormalizer normalizer = new TimeNormalizer();
        for (String text : corpus) {
            text = entityRecognizePreprocess(text);
            TimeUnit[] unit = normalizer.parse(text);//对于上/下的识别
            List<TimeUnit> timeUnitList = Stream.of(unit).filter(item -> StringUtils.isNotEmpty(item.Time_Norm))
                    .collect(Collectors
                            .toList());
            List<TimeEntity> timeEntityList = timeEntityRecognizer.parse(text);
            if (timeUnitList.size() != timeEntityList.size()) {
                LOGGER.debug("{} -> should {}, but {}", text, timeUnitList, timeEntityList);
            } else if (timeEntityList.size() > 0) {
                if (!timeUnitList.get(0).getTime().equals(timeEntityList.get(0).getValue())) {
                    LOGGER.debug("========== {} -> should {}, but {}", text, timeUnitList, timeEntityList);
                }
            }
        }
    }

    private String entityRecognizePreprocess(String text) {
        final Map<Character, Integer> digitsMap = new HashMap<>();
        digitsMap.put('０', 0);
        digitsMap.put('１', 1);
        digitsMap.put('２', 2);
        digitsMap.put('３', 3);
        digitsMap.put('４', 4);
        digitsMap.put('５', 5);
        digitsMap.put('６', 6);
        digitsMap.put('７', 7);
        digitsMap.put('８', 8);
        digitsMap.put('９', 9);
        StringBuilder stringBuilder = new StringBuilder();
        char[] chars = text.toCharArray();
        for (char ch : chars) {
            if (digitsMap.containsKey(ch)) {
                stringBuilder.append(digitsMap.get(ch));
            } else {
                stringBuilder.append(ch);
            }
        }
        return stringBuilder.toString();
    }

    public List<String> getTestCorpus() throws IOException {
        URL url = getClass().getResource("/词性-词义_合并结果.txt");
        LineIterator lineIterator = IOUtils.lineIterator(new BufferedReader(new InputStreamReader(url.openStream())));
        List<String> list = new ArrayList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            line = StringUtils.stripToNull(line);
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            String[] splits = line.split("\\s");
            list.add(Stream.of(splits).map(text -> text.split("/")[0]).collect(Collectors.joining("")));
        }
        return list;
    }

//    @Test
    public void testRegexSplit() throws IOException {
        String patterns = "((前|昨|今|明|后)(天|日)?(早|晚)(晨|上|间)?)|(\\d+个?[年月日天][以之]?[前后])|(\\d+个?半?(小时|钟头|h|H))|(半个?(小时|钟头))|(\\d+(分钟|min))|([13]刻钟)|((上|这|本|下)+(周|星期)([一二三四五六七天日]|[1-7])?)|((周|星期)([一二三四五六七天日]|[1-7]))|((早|晚)?([0-2]?[0-9](点|时)半)(am|AM|pm|PM)?)|((早|晚)?(\\d+[:：]\\d+([:：]\\d+)*)\\s*(am|AM|pm|PM)?)|((早|晚)?([0-2]?[0-9](点|时)[13一三]刻)(am|AM|pm|PM)?)|((早|晚)?(\\d+[时点](\\d+)?分?(\\d+秒?)?)\\s*(am|AM|pm|PM)?)|(大+(前|后)天)|(([零一二三四五六七八九十百千万]+|\\d+)世)|([0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\\\d))([0-3][0-9]|[1-9]))|(现在)|(届时)|(这个月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)日)|(晚些时候)|(今年)|(长期)|(以前)|(过去)|(时期)|(时代)|(当时)|(近来)|(([零一二三四五六七八九十百千万]+|\\d+)夜)|(当前)|(日(数|多|多少|好几|几|差不多|近|前|后|上|左右))|((\\d+)点)|(今年([零一二三四五六七八九十百千万]+|\\d+))|(\\d+[:：]\\d+(分|))|((\\d+):(\\d+))|(\\d+/\\d+/\\d+)|(未来)|((充满美丽、希望、挑战的)?未来)|(最近)|(早上)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(日前)|(新世纪)|(小时)|(([0-3][0-9]|[1-9])(日|号))|(明天)|(\\d+)月|(([0-3][0-9]|[1-9])[日号])|((数|多|多少|好几|几|差不多|近|前|后|上|左右)周)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)年)|([一二三四五六七八九十百千万几多]+[天日周月年][后前左右]*)|(每[年月日天小时分秒钟]+)|((\\d+分)+(\\d+秒)?)|([一二三四五六七八九十]+来?[岁年])|([新?|\\d*]世纪末?)|((\\d+)时)|(世纪)|(([零一二三四五六七八九十百千万]+|\\d+)岁)|(今年)|([星期周]+[一二三四五六七])|(星期([零一二三四五六七八九十百千万]+|\\d+))|(([零一二三四五六七八九十百千万]+|\\d+)年)|([本后昨当新后明今去前那这][一二三四五六七八九十]?[年月日天])|(早|早晨|早上|上午|中午|午后|下午|晚上|晚间|夜里|夜|凌晨|深夜)|(回归前后)|((\\d+点)+(\\d+分)?(\\d+秒)?左右?)|((\\d+)年代)|(本月(\\d+))|(第(\\d+)天)|((\\d+)岁)|((\\d+)年(\\d+)月)|([去今明]?[年月](底|末))|(([零一二三四五六七八九十百千万]+|\\d+)世纪)|(昨天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(年度)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)星期)|(年底)|([下个本]+赛季)|(\\d+)月(\\d+)日|(\\d+)月(\\d+)|(今年(\\d+)月(\\d+)日)|((\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(今年晚些时候)|(两个星期)|(过去(数|多|多少|好几|几|差不多|近|前|后|上|左右)周)|(本赛季)|(半个(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(稍晚)|((\\d+)号晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)年)|(这个时候)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个小时)|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(凌晨)|((\\d+)年(\\d+)月(\\d+)日)|((\\d+)个月)|(今天早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(第[一二三四五六七八九十\\d+]+季)|(当地时间)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)年)|(早晨)|(一段时间)|([本上]周[一二三四五六七])|(凌晨(\\d+)点)|(去年(\\d+)月(\\d+)日)|(年关)|(如今)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(当晚)|((\\d+)日晚(\\d+)时)|(([零一二三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(每年(\\d+)月(\\d+)日)|(([零一二三四五六七八九十百千万]+|\\d+)周)|((\\d+)月)|(农历)|(两个小时)|(本周([零一二三四五六七八九十百千万]+|\\d+))|(长久)|(清晨)|((\\d+)号晚)|(春节)|(星期日)|(圣诞)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)段)|(现年)|(当日)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)分钟)|(\\d+(天|日|周|月|年)(后|前|))|((文艺复兴|巴洛克|前苏联|前一|暴力和专制|成年时期|古罗马|我们所处的敏感)+时期)|((\\d+)[年月天])|(清早)|(两年)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(昨天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(([零一二三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+))|(圣诞节)|(学期)|(\\d+来?分钟)|(过去(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(星期天)|(夜间)|((\\d+)日凌晨)|(([零一二三四五六七八九十百千万]+|\\d+)月底)|(当天)|((\\d+)日)|(((10)|(11)|(12)|([1-9]))月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(今年(\\d+)月份)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)时)|(连[年月日夜])|((\\d+)年(\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|((一|二|两|三|四|五|六|七|八|九|十|百|千|万|几|多|上|\\d+)+个?(天|日|周|月|年)(后|前|半|))|((胜利的)日子)|(青春期)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|([0-9]{4}年)|(周末)|(([零一二三四五六七八九十百千万]+|\\d+)个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(([(小学)|初中?|高中?|大学?|研][一二三四五六七八九十]?(\\d+)?)?[上下]半?学期)|(([零一二三四五六七八九十百千万]+|\\d+)时期)|(午间)|(次年)|(这时候)|(农历新年)|([春夏秋冬](天|季))|((\\d+)天)|(元宵节)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)分)|((\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)时(\\d+)分)|(傍晚)|(周([零一二三四五六七八九十百千万]+|\\d+))|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时(\\d+)分)|(同日)|((\\d+)年(\\d+)月底)|((\\d+)分钟)|((\\d+)世纪)|(冬季)|(国庆)|(年代)|(([零一二三四五六七八九十百千万]+|\\d+)年半)|(今年年底)|(新年)|(本周)|(当地时间星期([零一二三四五六七八九十百千万]+|\\d+))|(([零一二三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)岁)|(半小时)|(每周)|(([零一二三四五六七八九十百千万]+|\\d+)周年)|((重要|最后)?时刻)|(([零一二三四五六七八九十百千万]+|\\d+)期间)|(周日)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(今后)|(([零一二三四五六七八九十百千万]+|\\d+)段时间)|(明年)|([12][09][0-9]{2}(年度?))|(([零一二三四五六七八九十百千万]+|\\d+)生)|(今天凌晨)|(过去(\\d+)年)|(元月)|((\\d+)月(\\d+)日凌晨)|([前去今明后新]+年)|((\\d+)月(\\d+))|(夏天)|((\\d+)日凌晨(\\d+)时许)|((\\d+)月(\\d+)日)|((\\d+)点半)|(去年底)|(最后一[天刻])|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)个月)|(圣诞节?)|(下?个?(星期|周)(一|二|三|四|五|六|七|天))|((\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(当天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(每年的(\\d+)月(\\d+)日)|((\\d+)日晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(星期([零一二三四五六七八九十百千万]+|\\d+)晚)|(深夜)|(现如今)|([上中下]+午)|(第(一|二|三|四|五|六|七|八|九|十|百|千|万|几|多|\\d+)+个?(天|日|周|月|年))|(昨晚)|(近年)|(今天清晨)|(中旬)|(星期([零一二三四五六七八九十百千万]+|\\d+)早)|(([零一二三四五六七八九十百千万]+|\\d+)战期间)|(星期)|(昨天晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(较早时)|(个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|((民主高中|我们所处的|复仇主义和其它危害人类的灾难性疾病盛行的|快速承包电影主权的|恢复自我美德|人类审美力基础设施|饱受暴力、野蛮、流血、仇恨、嫉妒的|童年|艰苦的童年)+时代)|(元旦)|(([零一二三四五六七八九十百千万]+|\\d+)个礼拜)|(昨日)|([年月]初)|((\\d+)年的(\\d+)月)|(每年)|(([零一二三四五六七八九十百千万]+|\\d+)月份)|(今年(\\d+)月(\\d+)号)|(今年([零一二三四五六七八九十百千万]+|\\d+)月)|((\\d+)月底)|(未来(\\d+)年)|(第([零一二三四五六七八九十百千万]+|\\d+)季)|(\\d?多年)|(([零一二三四五六七八九十百千万]+|\\d+)个星期)|((\\d+)年([零一二三四五六七八九十百千万]+|\\d+)月)|([下上中]午)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)点)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)月)|(([零一二三四五六七八九十百千万]+|\\d+)个(数|多|多少|好几|几|差不多|近|前|后|上|左右)月)|(同([零一二三四五六七八九十百千万]+|\\d+)天)|((\\d+)号凌晨)|(夜里)|(两个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(昨天)|(罗马时代)|(目(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(([零一二三四五六七八九十百千万]+|\\d+)月)|((\\d+)年(\\d+)月(\\d+)号)|(((10)|(11)|(12)|([1-9]))月份?)|([12][0-9]世纪)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)天)|(工作日)|(稍后)|((\\d+)号(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(未来([零一二三四五六七八九十百千万]+|\\d+)年)|([0-9]+[天日周月年][后前左右]*)|(([零一二三四五六七八九十百千万]+|\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)刻)|(很久)|((\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)岁)|(去年(\\d+)月(\\d+)号)|(两个月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(古代)|(两天)|(\\d+个?(小时|星期))|((\\d+)年半)|(较早)|(([零一二三四五六七八九十百千万]+|\\d+)个小时)|([一二三四五六七八九十]+周年)|(星期([零一二三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(时刻)|((\\d+天)+(\\d+点)?(\\d+分)?(\\d+秒)?)|((\\d+)日([零一二三四五六七八九十百千万]+|\\d+)时)|((\\d+)周年)|(([零一二三四五六七八九十百千万]+|\\d+)早)|(([零一二三四五六七八九十百千万]+|\\d+)日)|(去年(\\d+)月)|(过去([零一二三四五六七八九十百千万]+|\\d+)年)|((\\d+)个星期)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)天)|(执政期间)|([当前昨今明后春夏秋冬]+天)|(去年(\\d+)月份)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右))|((\\d+)周)|(两星期)|(([零一二三四五六七八九十百千万]+|\\d+)年代)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)天)|(昔日)|(两个半月)|([印尼|北京|美国]?当地时间)|(连日)|(本月(\\d+)日)|(第([零一二三四五六七八九十百千万]+|\\d+)天)|((\\d+)点(\\d+)分)|([长近多]年)|((\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(那时)|(冷战时代)|(([零一二三四五六七八九十百千万]+|\\d+)天)|(这个星期)|(去年)|(昨天傍晚)|(近期)|(星期([零一二三四五六七八九十百千万]+|\\d+)早些时候)|((\\d+)([零一二三四五六七八九十百千万]+|\\d+)年)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)两个月)|((\\d+)个小时)|(([零一二三四五六七八九十百千万]+|\\d+)个月)|(当年)|(本月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二三四五六七八九十百千万]+|\\d+)个月)|((\\d+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(目前)|(去年([零一二三四五六七八九十百千万]+|\\d+)月)|((\\d+)时(\\d+)分)|(每月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)段时间)|((\\d+)日晚)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(下旬)|((\\d+)月份)|(逐年)|(稍(数|多|多少|好几|几|差不多|近|前|后|上|左右))|((\\d+)年)|(月底)|(这个月)|((\\d+)年(\\d+)个月)|(\\d+大寿)|(周([零一二三四五六七八九十百千万]+|\\d+)早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(半年)|(今日)|(末日)|(昨天深夜)|(今年(\\d+)月)|((\\d+)月(\\d+)号)|((\\d+)日夜)|((早些|某个|晚间|本星期早些|前些)+时候)|(同年)|((北京|那个|更长的|最终冲突的)时间)|(每个月)|(一早)|((\\d+)来?[岁年])|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个月)|([鼠牛虎兔龙蛇马羊猴鸡狗猪]年)|(季度)|(早些时候)|(今天)|(每天)|(年半)|(下(个)?月)|(午后)|((\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个星期)|(今天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(同[一二三四五六七八九十][年|月|天])|(T\\d+:\\d+:\\d+)|(\\d+/\\d+/\\d+:\\d+:\\d+.\\d+)|(\\?\\?\\?\\?-\\?\\?-\\?\\?T\\d+:\\d+:\\d+)|(\\d+-\\d+-\\d+T\\d+:\\d+:\\d+)|(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+.\\d+)|(\\d+-\\d+-\\d+|[0-9]{8})|(((\\d+)年)?((10)|(11)|(12)|([1-9]))月(\\d+))|((\\d[\\.\\-])?((10)|(11)|(12)|([1-9]))[\\.\\-](\\d+))";

        int depth = 0;
        int lastIndex = 0;
        for (int i = 0; i < patterns.length(); i++) {
            char ch = patterns.charAt(i);
            if ('(' == ch) {
                depth++;
            } else if (')' == ch) {
                depth--;
            } else if ('|' == ch) {
                if (depth == 0) {
                    LOGGER.info("regex split:\t{}\t{}\t{}", lastIndex, i, patterns.substring(lastIndex, i));
                    lastIndex = i + 1;
                } else {
                    //pass
                }
            }

        }
    }
}
