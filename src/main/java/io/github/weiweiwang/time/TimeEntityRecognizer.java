package io.github.weiweiwang.time;

import io.github.weiweiwang.number.ChineseNumbers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wangweiwei01 on 17/4/19.
 */

/**
 * thread safe
 */
public class TimeEntityRecognizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeEntityRecognizer.class);
    private static final String DEFAULT_PATTERN_STRING = "((前|昨|今|明|后)(天|日)?(早|晚)(晨|上|间)?)|(\\d+个?[年月日天][以之]?[前后])|(\\d+个?半?(小时|钟头|h|H))|(半个?(小时|钟头))|([\\d一二三四五六七八九十]+(分钟|min))|([13]刻钟)|((上|这|本|下)+(周|星期)([一二三四五六七天日]|[1-7])?)|((周|星期)([一二三四五六七天日]|[1-7]))|((早|晚)?([\\d一二三四五六七八九十]+(点|时)半)(am|AM|pm|PM)?)|((早|晚)?(\\d+[:：]\\d+([:：]\\d+)*)\\s*(am|AM|pm|PM)?)|((早|晚)?([\\d一二三四五六七八九十]+(点|时)[13一三]刻)(am|AM|pm|PM)?)|((早|晚)?([\\d一二三四五六七八九十]+[时点]([\\d一二三四五六七八九十]+)?分?([\\d一二三四五六七八九十]+秒?)?)\\s*(am|AM|pm|PM)?)|(大+(前|后)天)|(([零一二两三四五六七八九十百千万]+|\\d+)世)|([0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\\\d))([0-3][0-9]|[1-9]))|(现在)|(届时)|(这个月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)日)|(晚些时候)|(今年)|(长期)|(以前)|(过去)|(时期)|(时代)|(当时)|(近来)|(([零一二两三四五六七八九十百千万]+|\\d+)夜)|(当前)|(日(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(([\\d一二三四五六七八九十]+)点)|(今年([零一二两三四五六七八九十百千万]+|\\d+))|(\\d+[:：]\\d+(分|))|((\\d+):(\\d+))|(\\d+/\\d+/\\d+)|(未来)|((充满美丽、希望、挑战的)?未来)|(最近)|(早上)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(日前)|(新世纪)|(小时)|(([0-3][0-9]|[1-9])(日|号))|(明天)|(\\d+)月|(([0-3][0-9]|[1-9])[日号])|((数|多|多少|好几|几|差不多|近|前|后|上|左右)周)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|\\d+)年)|([一二三四五六七八九十百千万几多]+[天日周月年][后前左右]*)|(每[年月日天小时分秒钟]+)|((\\d+分)+(\\d+秒)?)|([一二三四五六七八九十]+来?[岁年])|([新?|\\d*]世纪末?)|((\\d+)时)|(世纪)|(([零一二两三四五六七八九十百千万]+|\\d+)岁)|(今年)|([星期周]+[一二三四五六七])|(星期([零一二两三四五六七八九十百千万]+|\\d+))|(([零一二两三四五六七八九十百千万]+|\\d+)年)|([本后昨当新后明今去前那这][一二三四五六七八九十]?[年月日天])|(早|早晨|早上|上午|中午|午后|下午|晚上|晚间|夜里|夜|凌晨|深夜)|(回归前后)|(([\\d一二三四五六七八九十]+点)+(\\d+分)?(\\d+秒)?左右?)|((\\d+)年代)|(本月(\\d+))|(第(\\d+)天)|((\\d+)岁)|((\\d+)年(\\d+)月)|([去今明]?[年月](底|末))|(([零一二两三四五六七八九十百千万]+|\\d+)世纪)|(昨天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(年度)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)星期)|(年底)|([下个本]+赛季)|(\\d+)月(\\d+)日|(\\d+)月(\\d+)|(今年(\\d+)月(\\d+)日)|((\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(今年晚些时候)|(两个星期)|(过去(数|多|多少|好几|几|差不多|近|前|后|上|左右)周)|(本赛季)|(半个(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(稍晚)|((\\d+)号晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)年)|(这个时候)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个小时)|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(凌晨)|((\\d+)年(\\d+)月(\\d+)日)|((\\d+)个月)|(今天早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(第[一二三四五六七八九十\\d+]+季)|(当地时间)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|\\d+)年)|(早晨)|(一段时间)|([本上]周[一二三四五六七])|(凌晨([\\d一二三四五六七八九十]+)点)|(去年(\\d+)月(\\d+)日)|(年关)|(如今)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(当晚)|((\\d+)日晚(\\d+)时)|(([零一二两三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(每年(\\d+)月(\\d+)日)|(([零一二两三四五六七八九十百千万]+|\\d+)周)|((\\d+)月)|(农历)|(两个小时)|(本周([零一二两三四五六七八九十百千万]+|\\d+))|(长久)|(清晨)|((\\d+)号晚)|(春节)|(星期日)|(圣诞)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)段)|(现年)|(当日)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)分钟)|(\\d+(天|日|周|月|年)(后|前|))|((文艺复兴|巴洛克|前苏联|前一|暴力和专制|成年时期|古罗马|我们所处的敏感)+时期)|((\\d+)[年月天])|(清早)|(两年)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(昨天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(([零一二两三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+))|(圣诞节)|(学期)|(\\d+来?分钟)|(过去(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(星期天)|(夜间)|((\\d+)日凌晨)|(([零一二两三四五六七八九十百千万]+|\\d+)月底)|(当天)|((\\d+)日)|(((10)|(11)|(12)|([1-9]))月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(今年(\\d+)月份)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)时)|(连[年月日夜])|((\\d+)年(\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|((一|二|两|三|四|五|六|七|八|九|十|百|千|万|几|多|上|\\d+)+个?(天|日|周|月|年)(后|前|半|))|((胜利的)日子)|(青春期)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|[\\d一二三四五六七八九十]+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|([0-9]{4}年)|(周末)|(([零一二两三四五六七八九十百千万]+|\\d+)个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(([(小学)|初中?|高中?|大学?|研][一二三四五六七八九十]?(\\d+)?)?[上下]半?学期)|(([零一二两三四五六七八九十百千万]+|\\d+)时期)|(午间)|(次年)|(这时候)|(农历新年)|([春夏秋冬](天|季))|((\\d+)天)|(元宵节)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)分)|((\\d+)月(\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右)(\\d+)时(\\d+)分)|(傍晚)|(周([零一二两三四五六七八九十百千万]+|\\d+))|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时(\\d+)分)|(同日)|((\\d+)年(\\d+)月底)|((\\d+)分钟)|((\\d+)世纪)|(冬季)|(国庆)|(年代)|(([零一二两三四五六七八九十百千万]+|\\d+)年半)|(今年年底)|(新年)|(本周)|(当地时间星期([零一二两三四五六七八九十百千万]+|\\d+))|(([零一二两三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)岁)|(半小时)|(每周)|(([零一二两三四五六七八九十百千万]+|\\d+)周年)|((重要|最后)?时刻)|(([零一二两三四五六七八九十百千万]+|\\d+)期间)|(周日)|(晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(今后)|(([零一二两三四五六七八九十百千万]+|\\d+)段时间)|(明年)|([12][09][0-9]{2}(年度?))|(([零一二两三四五六七八九十百千万]+|\\d+)生)|(今天凌晨)|(过去(\\d+)年)|(元月)|((\\d+)月(\\d+)日凌晨)|([前去今明后新]+年)|((\\d+)月(\\d+))|(夏天)|((\\d+)日凌晨(\\d+)时许)|((\\d+)月(\\d+)日)|(([\\d一二三四五六七八九十]+)点半)|(去年底)|(最后一[天刻])|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)个月)|(圣诞节?)|(下?个?(星期|周)(一|二|三|四|五|六|七|天))|((\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)年)|(当天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(每年的(\\d+)月(\\d+)日)|((\\d+)日晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(星期([零一二两三四五六七八九十百千万]+|\\d+)晚)|(深夜)|(现如今)|([上中下]+午)|(第(一|二|三|四|五|六|七|八|九|十|百|千|万|几|多|\\d+)+个?(天|日|周|月|年))|(昨晚)|(近年)|(今天清晨)|(中旬)|(星期([零一二两三四五六七八九十百千万]+|\\d+)早)|(([零一二两三四五六七八九十百千万]+|\\d+)战期间)|(星期)|(昨天晚(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(较早时)|(个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|((民主高中|我们所处的|复仇主义和其它危害人类的灾难性疾病盛行的|快速承包电影主权的|恢复自我美德|人类审美力基础设施|饱受暴力、野蛮、流血、仇恨、嫉妒的|童年|艰苦的童年)+时代)|(元旦)|(([零一二两三四五六七八九十百千万]+|\\d+)个礼拜)|(昨日)|([年月]初)|((\\d+)年的(\\d+)月)|(每年)|(([零一二两三四五六七八九十百千万]+|\\d+)月份)|(今年(\\d+)月(\\d+)号)|(今年([零一二两三四五六七八九十百千万]+|\\d+)月)|((\\d+)月底)|(未来(\\d+)年)|(第([零一二两三四五六七八九十百千万]+|\\d+)季)|(\\d?多年)|(([零一二两三四五六七八九十百千万]+|\\d+)个星期)|((\\d+)年([零一二两三四五六七八九十百千万]+|\\d+)月)|([下上中]午)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)([\\d一二三四五六七八九十]+)点)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)月)|(([零一二两三四五六七八九十百千万]+|\\d+)个(数|多|多少|好几|几|差不多|近|前|后|上|左右)月)|(同([零一二两三四五六七八九十百千万]+|\\d+)天)|((\\d+)号凌晨)|(夜里)|(两个(数|多|多少|好几|几|差不多|近|前|后|上|左右)小时)|(昨天)|(罗马时代)|(目(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(([零一二两三四五六七八九十百千万]+|\\d+)月)|((\\d+)年(\\d+)月(\\d+)号)|(((10)|(11)|(12)|([1-9]))月份?)|([12][0-9]世纪)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|\\d+)天)|(工作日)|(稍后)|((\\d+)号(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(未来([零一二两三四五六七八九十百千万]+|\\d+)年)|([0-9]+[天日周月年][后前左右]*)|(([零一二两三四五六七八九十百千万]+|\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(最(数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|\\d+)刻)|(很久)|((\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)岁)|(去年(\\d+)月(\\d+)号)|(两个月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(古代)|(两天)|(\\d+个?(小时|星期))|((\\d+)年半)|(较早)|(([零一二两三四五六七八九十百千万]+|\\d+)个小时)|([一二三四五六七八九十]+周年)|(星期([零一二两三四五六七八九十百千万]+|\\d+)(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(时刻)|((\\d+天)+([\\d一二三四五六七八九十]+点)?(\\d+分)?(\\d+秒)?)|((\\d+)日([零一二两三四五六七八九十百千万]+|\\d+)时)|((\\d+)周年)|(([零一二两三四五六七八九十百千万]+|\\d+)早)|(([零一二两三四五六七八九十百千万]+|\\d+)日)|(去年(\\d+)月)|(过去([零一二两三四五六七八九十百千万]+|\\d+)年)|((\\d+)个星期)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)(数|多|多少|好几|几|差不多|近|前|后|上|左右)天)|(执政期间)|([当前昨今明后春夏秋冬]+天)|(去年(\\d+)月份)|(今(数|多|多少|好几|几|差不多|近|前|后|上|左右))|((\\d+)周)|(两星期)|(([零一二两三四五六七八九十百千万]+|\\d+)年代)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)天)|(昔日)|(两个半月)|([印尼|北京|美国]?当地时间)|(连日)|(本月(\\d+)日)|(第([零一二两三四五六七八九十百千万]+|\\d+)天)|(([\\d一二三四五六七八九十]+)点(\\d+)分)|([长近多]年)|((\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午(\\d+)时)|(那时)|(冷战时代)|(([零一二两三四五六七八九十百千万]+|\\d+)天)|(这个星期)|(去年)|(昨天傍晚)|(近期)|(星期([零一二两三四五六七八九十百千万]+|\\d+)早些时候)|((\\d+)([零一二两三四五六七八九十百千万]+|\\d+)年)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)两个月)|((\\d+)个小时)|(([零一二两三四五六七八九十百千万]+|\\d+)个月)|(当年)|(本月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)([零一二两三四五六七八九十百千万]+|\\d+)个月)|(([\\d一二三四五六七八九十]+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(目前)|(去年([零一二两三四五六七八九十百千万]+|\\d+)月)|((\\d+)时(\\d+)分)|(每月)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)段时间)|((\\d+)日晚)|(早(数|多|多少|好几|几|差不多|近|前|后|上|左右)([\\d一二三四五六七八九十]+)点(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(下旬)|((\\d+)月份)|(逐年)|(稍(数|多|多少|好几|几|差不多|近|前|后|上|左右))|((\\d+)年)|(月底)|(这个月)|((\\d+)年(\\d+)个月)|(\\d+大寿)|(周([零一二两三四五六七八九十百千万]+|\\d+)早(数|多|多少|好几|几|差不多|近|前|后|上|左右))|(半年)|(今日)|(末日)|(昨天深夜)|(今年(\\d+)月)|((\\d+)月(\\d+)号)|((\\d+)日夜)|((早些|某个|晚间|本星期早些|前些)+时候)|(同年)|((北京|那个|更长的|最终冲突的)时间)|(每个月)|(一早)|((\\d+)来?[岁年])|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个月)|([鼠牛虎兔龙蛇马羊猴鸡狗猪]年)|(季度)|(早些时候)|(今天)|(每天)|(年半)|(下(个)?月)|(午后)|((\\d+)日(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|((数|多|多少|好几|几|差不多|近|前|后|上|左右)个星期)|(今天(数|多|多少|好几|几|差不多|近|前|后|上|左右)午)|(同[一二三四五六七八九十][年|月|天])|(T\\d+:\\d+:\\d+)|(\\d+/\\d+/\\d+:\\d+:\\d+.\\d+)|(\\?\\?\\?\\?-\\?\\?-\\?\\?T\\d+:\\d+:\\d+)|(\\d+-\\d+-\\d+T\\d+:\\d+:\\d+)|(\\d+/\\d+/\\d+ \\d+:\\d+:\\d+.\\d+)|(\\d+-\\d+-\\d+|[0-9]{8})|(((\\d+)年)?((10)|(11)" +
            "|(12)|([1-9]))月(\\d+))|((\\d[\\.\\-])?((10)|(11)|(12)|([1-9]))[\\.\\-](\\d+))";
    private Pattern pattern;

    public TimeEntityRecognizer() {
        this(DEFAULT_PATTERN_STRING);
    }

    public TimeEntityRecognizer(String pattern) {
        this.pattern = Pattern.compile(pattern);
    }

    public List<TimeEntity> parse(String text) {
        return parse(text, Calendar.getInstance().getTime());
    }

    public List<TimeEntity> parse(String text, Date relative) {
        List<TimeEntity> result = new ArrayList<>();
        int offset;
        Matcher match = pattern.matcher(text);
        while (match.find()) {
            TimeEntity lastEntity = result.isEmpty() ? null : result.get(result.size() - 1);
            offset = match.start();
            String matchedText = match.group();
            if (lastEntity != null && offset == lastEntity.getOffset() + lastEntity.getOriginal().length()) {
                lastEntity.setOriginal(lastEntity.getOriginal() + matchedText);
            } else {
                TimeEntity timeEntity = new TimeEntity(matchedText, offset);
                result.add(timeEntity);
            }
        }
        Iterator<TimeEntity> iterator = result.iterator();
        while (iterator.hasNext()) {
            TimeEntity timeEntity = iterator.next();
            Date date = parseTime(timeEntity.getOriginal(), relative);
            if (null != date) {
                timeEntity.setValue(date);
            } else {
                iterator.remove();
            }
        }
//        for (TimeEntity timeEntity : result) {
//            timeEntity.setValue(parseTime(timeEntity.getOriginal(), relative));
//        }
        return result;
    }


    private static final Pattern YEAR_2_DIGIT_PATTERN = Pattern.compile("[0-9]{2}(?=年)");
    private static final Pattern YEAR_4_DIGIT_PATTERN = Pattern.compile("[0-9]?[0-9]{3}(?=年)");

    private String replaceChineseNumber(String text) {
        text = text.replace("周日", "周7");
        Pattern p = Pattern.compile("[一二两三四五六七八九十]+");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            String group = m.group();
            Number number = ChineseNumbers.chineseNumberToEnglish(group);
            m.appendReplacement(sb, String.valueOf(number.intValue()));
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private boolean validTime(int arr[]) {
        int sum = Arrays.stream(arr).sum();
        return sum != -6;
    }

    private Date parseTime(String text, Date relative) {
        text = replaceChineseNumber(text);
        int year = parseYear(text);
        int month = parseMonth(text);
        int day = parseDay(text);
        int hour = parseHour(text);
        int minute = parseMinute(text);
        int second = parseSecond(text);
        int[] arr = {year, month, day, hour, minute, second};
        overallParse(text, arr);
        parseRelative(text, relative, arr);
        parseCurrentRelative(text, relative, arr);

        if (!validTime(arr)) {
            return null;
        }
        normalize(arr, relative);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        final int[] fields = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar
                .MINUTE, Calendar.SECOND};
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                calendar.set(fields[i], arr[i]);
            }
        }
        if (arr[1] > 0) {
            calendar.set(Calendar.MONTH, arr[1] - 1);
        }
        return calendar.getTime();
    }

    /**
     * 将第一个下标对应数值为正的前面几个字段都设置为相对时间的对应值
     *
     * @param arr
     * @param relative
     */
    private void normalize(int[] arr, Date relative) {
        int j = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > 0) {
                j = i;
                break;
            }
        }
        Calendar calender = Calendar.getInstance();
        calender.setTime(relative);
        final int[] fields = {Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, Calendar
                .MINUTE, Calendar.SECOND};
        for (int i = 0; i < j; i++) {
            if (arr[i] < 0) {
                if (i == 1) {
                    arr[i] = calender.get(Calendar.MONTH) + 1;
                } else {
                    arr[i] = calender.get(fields[i]);
                }
            }
        }
    }

    private int parseYear(String text) {
        int year = -1;
        /*
         * 不仅局限于支持1XXX年和2XXX年的识别，可识别三位数和四位数表示的年份
		 * modified by 曹零
		 */
        Matcher match = YEAR_4_DIGIT_PATTERN.matcher(text);
        if (match.find()) {
            year = Integer.parseInt(match.group());
        } else {
            match = YEAR_2_DIGIT_PATTERN.matcher(text);
            if (match.find()) {
                year = Integer.parseInt(match.group());
                if (year >= 0 && year < 100) {
                    if (year < 30) {
                        year += 2000;
                    } else {
                        year += 1900;
                    }
                }

            }
        }

        return year;
    }

    private static final Pattern MONTH_PATTERN = Pattern.compile("((10)|(11)|(12)|([1-9]))(?=月)");

    private int parseMonth(String text) {
        Matcher match = MONTH_PATTERN.matcher(text);
        if (match.find()) {
            return Integer.parseInt(match.group());
        }
        return -1;
    }

    private static final Pattern DAY_PATTERN = Pattern.compile("((?<!\\d))([0-3][0-9]|[1-9])(?=(日|号))");

    private int parseDay(String text) {
        Matcher match = DAY_PATTERN.matcher(text);
        if (match.find()) {
            return Integer.parseInt(match.group());
        }
        return -1;
    }


    private static final Pattern HOUR_PATTERN = Pattern.compile("(?<!(周|星期))([0-2]?[0-9])(?=(点|时))");
    private static final Pattern NOON_PATTERN = Pattern.compile("(中午)|(午间)");
    private static final Pattern AFTERNOON_PATTERN = Pattern.compile("(下午)|(午后)|(pm)|(PM)");
    private static final Pattern NIGHT_PATTERN = Pattern.compile("晚");

    /**
     * @param text
     * @return
     */
    private int parseHour(String text) {
        /*
         * 清除只能识别11-99时的bug
		 * modified by 曹零
		 */

        int hour = -1;
        Matcher match = HOUR_PATTERN.matcher(text);
        if (match.find()) {
            hour = Integer.parseInt(match.group());
        }
        /*
         * 对关键字：中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM的正确24小时时间计算
		 * 规约：
		 * 1.中午/午间0-10点视为12-22点
		 * 2.下午/午后0-11点视为12-23点
		 * 3.晚上/傍晚/晚间/晚1-11点视为13-23点，12点视为0点
		 * 4.0-11点pm/PM视为12-23点
		 *
		 * add by 曹零
		 */
        match = NOON_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 0 && hour <= 10)
                hour += 12;
        }

        match = AFTERNOON_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 0 && hour <= 11)
                hour += 12;
        }

        match = NIGHT_PATTERN.matcher(text);
        if (match.find()) {
            if (hour >= 1 && hour <= 11)
                hour += 12;
            else if (hour == 12)
                hour = 0;
        }
        return hour;
    }

    private static final Pattern MINUTE_PATTERN = Pattern.compile("([0-5]?[0-9](?=分(?!钟)))|((?<=((?<!小)[点时]))[0-5]?[0-9](?!刻))");
    private static final Pattern ONE_QUARTER_PATTERN = Pattern.compile("(?<=[点时])[1一]刻(?!钟)");
    private static final Pattern TWO_QUARTER_PATTERN = Pattern.compile("(?<=[点时])半");
    private static final Pattern THREE_QUARTER_PATTERN = Pattern.compile("(?<=[点时])[3三]刻(?!钟)");

    /**
     * @param text
     * @return
     */
    private int parseMinute(String text) {
        /*
         * 添加了省略“分”说法的时间
		 * 如17点15
		 * modified by 曹零
		 */
        int minute = -1;
        Matcher match = MINUTE_PATTERN.matcher(text);
        if (match.find()) {
            minute = Integer.parseInt(match.group());
        }
        /*
         * 添加对一刻，半，3刻的正确识别（1刻为15分，半为30分，3刻为45分）
		 *
		 * add by 曹零
		 */
        match = ONE_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 15;
        }

        match = TWO_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 30;
        }

        match = THREE_QUARTER_PATTERN.matcher(text);
        if (match.find()) {
            minute = 45;
        }
        return minute;
    }

    private static final Pattern SECOND_PATTERN = Pattern.compile("([0-5]?[0-9](?=秒))|((?<=分)[0-5]?[0-9])");

    private int parseSecond(String text) {
        /*
         * 添加了省略“分”说法的时间
		 * 如17点15分32
		 * modified by 曹零
		 */
        Matcher match = SECOND_PATTERN.matcher(text);
        if (match.find()) {
            return Integer.parseInt(match.group());
        }
        return -1;
    }

    private static final Pattern HOUR_MINUTE_SECOND_PATTERN = Pattern
            .compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]:[0-5]?[0-9]");

    private static final Pattern HOUR_MINUTE_PATTERN = Pattern.compile("(?<!(周|星期))([0-2]?[0-9]):[0-5]?[0-9]");

    private static final Pattern DASH_YEAR_MONTH_DAY = Pattern
            .compile("[0-9]?[0-9]?[0-9]{2}-((10)|(11)|(12)|([1-9]))-((?<!\\d))([0-3][0-9]|[1-9])");

    private static final Pattern SLASH_YEAR_MONTH_DAY = Pattern
            .compile("((10)|(11)|(12)|([1-9]))/((?<!\\d))([0-3][0-9]|[1-9])/[0-9]?[0-9]?[0-9]{2}");
    private static final Pattern DOT_YEAR_MONTH_DAY = Pattern
            .compile("[0-9]?[0-9]?[0-9]{2}\\.((10)|(11)|(12)|([1-9]))\\.((?<!\\d))([0-3][0-9]|[1-9])");

    private void overallParse(String text, int[] arr) {
        /*
         * 修改了函数中所有的匹配规则使之更为严格
		 * modified by 曹零
		 */
        Matcher match = HOUR_MINUTE_SECOND_PATTERN.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split(":");
            arr[3] = Integer.parseInt(splits[0]);
            arr[4] = Integer.parseInt(splits[1]);
            arr[5] = Integer.parseInt(splits[2]);
        } else {
        /*
         * 添加了省略秒的:固定形式的时间规则匹配
		 * add by 曹零
		 */
            match = HOUR_MINUTE_PATTERN.matcher(text);
            if (match.find()) {
                String[] splits = match.group().split(":");
                arr[3] = Integer.parseInt(splits[0]);
                arr[4] = Integer.parseInt(splits[1]);
            }
        }
        /*
         * 增加了:固定形式时间表达式的
		 * 中午,午间,下午,午后,晚上,傍晚,晚间,晚,pm,PM
		 * 的正确时间计算，规约同上
		 * add by 曹零
		 */
        match = NOON_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 0 && arr[3] <= 10)
                arr[3] += 12;
        }

        match = AFTERNOON_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 0 && arr[3] <= 11)
                arr[3] += 12;
        }

        match = NIGHT_PATTERN.matcher(text);
        if (match.find()) {
            if (arr[3] >= 1 && arr[3] <= 11)
                arr[3] += 12;
            else if (arr[3] == 12)
                arr[3] = 0;
        }


        match = DASH_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("-");
            arr[0] = Integer.parseInt(splits[0]);
            arr[1] = Integer.parseInt(splits[1]);
            arr[2] = Integer.parseInt(splits[2]);
        }

        match = SLASH_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("/");
            arr[1] = Integer.parseInt(splits[0]);
            arr[2] = Integer.parseInt(splits[1]);
            arr[0] = Integer.parseInt(splits[2]);
        }

		/*
         * 增加了:固定形式时间表达式 年.月.日 的正确识别
		 * add by 曹零
		 */
        match = DOT_YEAR_MONTH_DAY.matcher(text);
        if (match.find()) {
            String[] splits = match.group().split("\\.");
            arr[0] = Integer.parseInt(splits[0]);
            arr[1] = Integer.parseInt(splits[1]);
            arr[2] = Integer.parseInt(splits[2]);
        }
    }

    private static final Pattern DAYS_BEFORE_PATTERN = Pattern.compile("\\d+(?=天[以之]?前)");
    private static final Pattern DAYS_AFTER_PATTERN = Pattern.compile("\\d+(?=天[以之]?后)");
    private static final Pattern MONTH_BEFORE_PATTERN = Pattern.compile("\\d+(?=(个)?月[以之]?前)");
    private static final Pattern MONTH_AFTER_PATTERN = Pattern.compile("\\d+(?=(个)?月[以之]?后)");
    private static final Pattern YEAR_BEFORE_PATTERN = Pattern.compile("\\d+(?=年[以之]?前)");
    private static final Pattern YEAR_AFTER_PATTERN = Pattern.compile("\\d+(?=年[以之]?后)");

    private void parseRelative(String text, Date relative, int[] arr) {

        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(relative);
        boolean[] flag = {false, false, false};

        Matcher match = DAYS_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int day = Integer.parseInt(match.group());
            calendar.add(Calendar.DATE, -day);
            flag[2] = true;
        }

        match = DAYS_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int day = Integer.parseInt(match.group());
            calendar.add(Calendar.DATE, day);
            flag[2] = true;
        }

        match = MONTH_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int month = Integer.parseInt(match.group());
            calendar.add(Calendar.MONTH, -month);
            flag[1] = true;
        }

        match = MONTH_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int month = Integer.parseInt(match.group());
            calendar.add(Calendar.MONTH, month);
            flag[1] = true;
        }

        match = YEAR_BEFORE_PATTERN.matcher(text);
        if (match.find()) {
            int year = Integer.parseInt(match.group());
            calendar.add(Calendar.YEAR, -year);
            flag[0] = true;
        }

        match = YEAR_AFTER_PATTERN.matcher(text);
        if (match.find()) {
            int year = Integer.parseInt(match.group());
            calendar.add(Calendar.YEAR, year);
            flag[0] = true;
        }
//        if (!relative.equals(calendar.getTime())) {
//            arr[0] = calendar.get(Calendar.YEAR);
//            arr[1] = calendar.get(Calendar.MONTH) + 1;
//            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
//        }
        if (flag[0] || flag[1] || flag[2]) {
            arr[0] = calendar.get(Calendar.YEAR);
        }
        if (flag[1] || flag[2]) {
            arr[1] = calendar.get(Calendar.MONTH) + 1;
        }
        if (flag[2]) {
            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }


    private static final Pattern LAST_MONTH_PATTERN = Pattern.compile("上(个)?月");
    private static final Pattern THIS_MONTH_PATTERN = Pattern.compile("(本|这个)月");
    private static final Pattern NEXT_MONTH_PATTERN = Pattern.compile("下(个)?月");
    private static final Pattern DAY_BEFORE_YESTERDAY_PATTERN = Pattern.compile("(?<!大)前天");
    private static final Pattern TODAY_PATTERN = Pattern.compile("今(?!年)");
    private static final Pattern TOMORROW_PATTERN = Pattern.compile("明(?!年)");
    private static final Pattern DAY_AFTER_TOMORROW_PATTERN = Pattern.compile("(?<!大)后天");
    private static final Pattern BEFORE_LAST_WEEKDAY_PATTERN = Pattern.compile("(?<=(上上(周|星期)))[1-7]");
    private static final Pattern LAST_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!上)上(周|星期)))[1-7]");
    private static final Pattern NEXT_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!下)下(周|星期)))[1-7]");
    private static final Pattern NEXT_NEXT_WEEKDAY_PATTERN = Pattern.compile("(?<=(下下(周|星期)))[1-7]");
    private static final Pattern THIS_WEEKDAY_PATTERN = Pattern.compile("(?<=((?<!(上|下))(周|星期)))[1-7]");

    /**
     * 设置当前时间相关的时间表达式
     * <p>
     * add by 曹零
     */
    public void parseCurrentRelative(String text, Date relative, int[] arr) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(relative);

        boolean[] flag = {false, false, false};//观察时间表达式是否因当前相关时间表达式而改变时间

        if (text.contains("前年")) {
            calendar.add(Calendar.YEAR, -2);
            flag[0] = true;
        }

        if (text.contains("去年")) {
            calendar.add(Calendar.YEAR, -1);
            flag[0] = true;
        }

        if (text.contains("今年")) {
            calendar.add(Calendar.YEAR, 0);
            flag[0] = true;
        }

        if (text.contains("明年")) {
            calendar.add(Calendar.YEAR, 1);
            flag[0] = true;
        }

        if (text.contains("后年")) {
            calendar.add(Calendar.YEAR, 2);
            flag[0] = true;
        }

        Matcher match = LAST_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, -1);
            flag[1] = true;
        }

        match = THIS_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, 0);
            flag[1] = true;
        }

        match = NEXT_MONTH_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.MONTH, 1);
            flag[1] = true;
        }

        if (text.contains("大前天")) {
            calendar.add(Calendar.DATE, -3);
            flag[2] = true;
        }

        match = DAY_BEFORE_YESTERDAY_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, -2);
            flag[2] = true;
        }

        if (text.contains("昨")) {
            calendar.add(Calendar.DATE, -1);
            flag[2] = true;
        }

        match = TODAY_PATTERN.matcher(text);
        if (match.find()) {
            flag[2] = true;
        }

        match = TOMORROW_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, 1);
            flag[2] = true;
        }

        match = DAY_AFTER_TOMORROW_PATTERN.matcher(text);
        if (match.find()) {
            calendar.add(Calendar.DATE, 2);
            flag[2] = true;
        }

        if (text.contains("大后天")) {
            calendar.add(Calendar.DATE, 3);
            flag[2] = true;
        }

        match = BEFORE_LAST_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) { // 周日=1，周二=2，。。。， 周六=7
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, -2);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }

        match = LAST_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, -1);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }

//        rule = "(?<=((?<!下)下(周|星期)))[1-7]";
        match = NEXT_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;

        }

//        rule = "(?<=(下下(周|星期)))[1-7]";
        match = NEXT_NEXT_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
            calendar.add(Calendar.WEEK_OF_MONTH, 2);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;

        }

//        rule = "(?<=((?<!(上|下))(周|星期)))[1-7]";
        match = THIS_WEEKDAY_PATTERN.matcher(text);
        if (match.find()) {
            int week = Integer.parseInt(match.group());
            if (week == 7) {
                week = 1;
            } else {
                week++;
            }
//            calendar.add(Calendar.WEEK_OF_MONTH, 0);
            calendar.set(Calendar.DAY_OF_WEEK, week);
            flag[2] = true;
        }
//        if (!relative.equals(calendar.getTime()) || text.contains("今")) {
//            arr[0] = calendar.get(Calendar.YEAR);
//            arr[1] = calendar.get(Calendar.MONTH) + 1;
//            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
//        }
//        String s = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(calendar.getTime());
        if (flag[0] || flag[1] || flag[2]) {
            arr[0] = calendar.get(Calendar.YEAR);
        }
        if (flag[1] || flag[2]) {
            arr[1] = calendar.get(Calendar.MONTH) + 1;
        }
        if (flag[2]) {
            arr[2] = calendar.get(Calendar.DAY_OF_MONTH);
        }
    }
}
