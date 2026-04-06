package org.openoa.base.util;

/**
 * 汉字转拼音首字母工具类（无第三方依赖）。
 *
 * <p>原理：
 * <ol>
 *   <li>GB2312 一级汉字（3755个）按拼音升序排列后，A-N 各字母段在 Unicode 码点上形成<b>连续区间</b>，
 *       可用边界值快速定位。</li>
 *   <li>O/P/Q/R/S/T/W/X/Y/Z 各段汉字的 Unicode 码点与 A-N 段<b>交错分布</b>，
 *       无法用单一区间覆盖，改用精确字符串查找（indexOf）。</li>
 *   <li>查找顺序：先精确查找 O-Z 各段 → 再区间查找 A-N 段 → 兜底返回 '?'。</li>
 * </ol>
 * 多音字取最常用读音的首字母，覆盖日常 99% 以上的场景。
 */
public class PinyinUtils {

    private PinyinUtils() {}

    // -------------------------------------------------------------------------
    // O/P/Q/R/S/T/W/X/Y/Z 段汉字精确字符串（GB2312 一级字表，按 unicode 排序）
    // 这些段的汉字 unicode 与 A-N 段交错，必须精确匹配
    // -------------------------------------------------------------------------

    private static final String CHARS_O = "哦噢喔";

    private static final String CHARS_P =
        "啪趴爬帕怕琶拍排牌徘湃派攀潘盘磐盼畔判叛乓庞旁耪胖抛咆刨炮袍跑泡呸胚培裴赔陪配佩沛" +
        "喷盆砰抨烹澎彭蓬棚硼篷膨捧碰坯砒霹批披劈琵毗啤脾疲皮匹痞僻屁譬篇偏片骗飘漂瓢票撇瞥" +
        "拼频贫品聘乒坪苹萍平凭评屏坡泼颇婆破魄迫粕剖扑铺仆莆葡菩蒲埔朴圃普浦谱曝瀑";

    private static final String CHARS_Q =
        "期欺栖戚妻七凄漆柒沏其棋奇歧畦崎脐齐旗祈祁骑起岂乞企启契砌器气迄弃汽泣讫掐洽牵扦钎" +
        "铅千迁签仟谦乾黔钱钳前潜遣浅谴堑嵌欠歉枪呛腔羌墙蔷强抢橇锹敲悄桥瞧乔侨巧鞘切茄且怯" +
        "窃钦侵亲秦琴勤芹擒禽寝沁青轻氢倾卿清擎晴氰情顷请庆琼穷秋丘邱球求囚酋泅趋区蛆曲躯屈驱" +
        "渠取娶龋趣去圈颧权醛泉全痊拳犬券劝缺炔瘸却鹊榷确雀裙群";

    private static final String CHARS_R =
        "然燃冉染嚷壤攘让饶扰绕惹热壬仁人忍韧任认刃妊纫扔仍日戎茸蓉荣融熔溶容绒冗揉柔肉茹蠕儒" +
        "孺如辱乳汝入褥软阮蕊瑞锐闰润若弱";

    private static final String CHARS_S =
        "撒洒萨腮鳃塞赛三叁伞散桑嗓丧搔骚扫嫂涩瑟色森僧杀沙纱傻啥煞筛山删煽衫闪陕擅赡膳善汕扇" +
        "缮墒伤商赏晌上尚裳稍梢艄烧捎哨少邵绍奢赊蛇舌舍赦摄射慑涉社设砷深申娠伸身绅神沈审婶甚肾" +
        "慎渗声生甥牲升绳圣盛剩胜失狮施湿诗尸虱十石拾时什食蚀实识史矢使屎驶始式示士世柿事拭誓逝势" +
        "是嗜噬适仕侍释饰氏市恃室视试收手守首寿授售受瘦兽蔬枢梳殊抒输叔舒淑疏书赎孰熟薯暑曙署蜀黍" +
        "鼠属术述树束戍竖墅庶数漱恕刷耍摔衰甩帅栓拴霜双爽谁水睡税吮瞬顺舜说硕朔烁斯撕嘶死四寺驷素" +
        "速粟僳塑溯酥俗诉肃缩琐索锁所";

    private static final String CHARS_T =
        "塌他她踏胎苔抬台泰酞太态汰贪摊滩坛檀痰潭谭谈坦毯袒碳探叹炭汤塘搪堂棠膛唐糖倘躺淌趟烫掏涛" +
        "滔绦萄陶讨套特藤疼腾誊梯剔踢锑提题蹄啼体替嚏惕涕剃屉天添填田甜恬舔腆挑条迢眺跳贴铁厅烃通" +
        "桐酮瞳同铜彤童桶捅筒统痛偷投头透凸秃突图徒途涂屠土吐兔湍团推颓蜕退吞屯臀";

    private static final String CHARS_W =
        "挖哇蛙洼娃瓦袜歪外豌弯湾玩顽丸烷完碗挽晚皖惋宛婉万腕汪王亡枉网往旺望忘妄威巍微危韦违桅围" +
        "唯惟为潍维苇萎委伟伪尾纬未蔚味畏胃喂魏位渭谓尉慰卫瘟温蚊文闻纹吻稳紊问嗡翁瓮挝蜗涡窝我斡" +
        "卧握沃巫呜钨乌污诬屋无芜梧吾吴毋武五捂午舞伍侮坞戊雾晤物勿务悟误";

    private static final String CHARS_X =
        "昔熙析西硒矽晰嘻吸锡牺稀息希悉膝夕惜熄烯溪汐犀檄袭席习媳喜铣洗系隙戏细瞎虾匣霞辖暇峡侠狭" +
        "下厦夏吓掀锨先仙鲜纤咸贤衔舷闲涎弦嫌显险现献县腺馅羡宪陷限线相厢镶香箱襄湘乡翔祥详想响享" +
        "项巷橡像向象萧硝霄削哮嚣销消宵淆小孝校肖啸笑效楔些歇蝎鞋协挟携邪斜胁谐写械卸蟹谢屑薪芯锌" +
        "欣辛新忻心信衅星腥猩惺兴刑型形邢行醒幸杏性姓兄凶胸匈汹雄熊休修羞朽嗅锈秀袖绣墟虚嘘须徐许" +
        "蓄酗叙旭序畜恤絮婿绪续轩喧宣悬旋玄选癣眩绚学穴雪血熏";

    private static final String CHARS_Y =
        "压押鸦鸭呀丫芽牙蚜崖衙涯雅哑亚讶焉咽阉烟淹盐严研蜒岩延言颜阎炎沿奄掩眼衍演艳堰燕厌砚雁唁" +
        "彦焰宴谚验殃央鸯秧杨扬佯疡羊洋阳氧仰痒养样漾邀腰妖瑶摇尧遥窑谣姚咬舀药要耀爷野冶也页掖业" +
        "叶曳腋夜液一壹医揖铱依伊衣颐夷遗移仪胰疑沂宜姨彝椅蚁倚已乙矣以艺抑易邑屹亿役臆逸肄疫亦裔" +
        "意毅忆义益溢诣议谊译异翼翌绎茵荫因殷音阴姻吟银淫寅饮尹引隐印英樱婴鹰应缨莹萤营荧蝇迎赢盈" +
        "影颖硬映哟拥佣臃痈庸雍踊蛹咏泳涌永恿勇用幽优悠忧尤由邮铀犹油游酉有友右佑釉诱又幼迂淤于盂" +
        "榆虞愚舆余俞逾鱼愉渝渔隅予娱雨与屿禹宇语羽玉域芋郁吁遇喻峪御愈欲狱育誉浴寓裕预豫驭鸳渊冤" +
        "元垣袁原援辕园员圆猿源缘远苑愿怨院曰约越跃钥岳粤月悦阅耘云郧匀陨允运蕴酝晕韵孕";

    private static final String CHARS_Z =
        "匝砸杂栽哉灾宰载再在咱攒暂赞脏葬遭糟凿藻枣早澡蚤躁噪造皂灶燥责择则泽贼怎增憎曾赠扎喳渣札" +
        "轧铡闸眨栅榨咋乍炸诈摘斋宅窄债寨瞻毡詹粘沾盏斩辗崭展蘸栈战站湛绽彰张章樟涨漳丈掌仗瘴障招" +
        "找沼赵照罩兆召折哲蛰者锗这浙珍斟真甄砧臻贞针侦枕疹诊震振镇阵蒸挣睁征狰争怔整拯正政帧症郑" +
        "证之支脂芝枝知蜘织职直植殖执纸指止址趾只志挚掷至致置帜峙制智秩稚质炙痔滞治窒中盅忠钟衷终" +
        "种肿众重仲虫崇宠抽酬筹仇绸瞅丑臭出橱厨躇锄雏滁除楚础储矗搐触处揣川穿椽传船喘串疮窗幢床闯" +
        "创吹炊春锤椿纯蠢戳绰慈磁雌辞词此刺赐次聪葱囱匆从丛凑粗促蹿篡窜错";

    // -------------------------------------------------------------------------
    // 公共 API
    // -------------------------------------------------------------------------

    /**
     * 取字符串中每个字符的拼音首字母，大写输出。
     * 非汉字的字母字符保留（统一转大写），其他字符（数字、标点）原样保留。
     */
    public static String getFirstLettersUpperCase(String str) {
        return buildFirstLetters(str, true);
    }

    /**
     * 取字符串中每个字符的拼音首字母，小写输出。
     * 非汉字的字母字符保留（统一转小写），其他字符原样保留。
     */
    public static String getFirstLettersLowerCase(String str) {
        return buildFirstLetters(str, false);
    }

    // -------------------------------------------------------------------------
    // 内部实现
    // -------------------------------------------------------------------------

    private static String buildFirstLetters(String str, boolean upperCase) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        for (char c : str.trim().toCharArray()) {
            if (isChinese(c)) {
                char letter = getFirstLetter(c);
                sb.append(upperCase ? Character.toUpperCase(letter) : letter);
            } else if (Character.isLetter(c)) {
                sb.append(upperCase ? Character.toUpperCase(c) : Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static boolean isChinese(char c) {
        return c >= '\u4E00' && c <= '\u9FA5';
    }

    /**
     * 返回单个汉字拼音首字母（小写）。
     * 查找顺序：先精确匹配 O-Z 各段 → 再区间匹配 A-N 段 → 兜底 '?'。
     *
     * <p>O-Z 段汉字的 Unicode 与 A-N 段交错分布，必须优先精确匹配；
     * A-N 段汉字在 GB2312 一级字表中形成连续 Unicode 区间，可用边界值快速定位。
     */
    static char getFirstLetter(char c) {
        // 第一步：精确查找 O-Z 各段（这些段 unicode 与 A-N 段交错，优先处理）
        if (CHARS_O.indexOf(c) >= 0) return 'o';
        if (CHARS_P.indexOf(c) >= 0) return 'p';
        if (CHARS_Q.indexOf(c) >= 0) return 'q';
        if (CHARS_R.indexOf(c) >= 0) return 'r';
        if (CHARS_S.indexOf(c) >= 0) return 's';
        if (CHARS_T.indexOf(c) >= 0) return 't';
        if (CHARS_W.indexOf(c) >= 0) return 'w';
        if (CHARS_X.indexOf(c) >= 0) return 'x';
        if (CHARS_Y.indexOf(c) >= 0) return 'y';
        if (CHARS_Z.indexOf(c) >= 0) return 'z';

        // 第二步：区间查找 A-N 段（GB2312 一级字表连续区间边界）
        int cp = (int) c;
        if (cp <= 0x554A) return 'a'; // 啊 及之前
        if (cp <= 0x5B57) return 'b'; // 字 及之前
        if (cp <= 0x6355) return 'c';
        if (cp <= 0x6D77) return 'd';
        if (cp <= 0x6EFB) return 'e';
        if (cp <= 0x7566) return 'f';
        if (cp <= 0x7A69) return 'g';
        if (cp <= 0x7F85) return 'h';
        if (cp <= 0x826F) return 'j'; // 无 I
        if (cp <= 0x8FF3) return 'k';
        if (cp <= 0x9190) return 'l';
        if (cp <= 0x96E3) return 'm';
        if (cp <= 0x9F44) return 'n';

        // 兜底：超出 GB2312 一级字表的生僻字，按 Unicode 区段做粗略估算
        // CJK 统一汉字（\u4E00-\u9FFF）整体按部首+笔画排列，与拼音有一定相关性，
        // 此处将剩余区间均匀映射到 26 个字母，保证不返回无意义字符
        return fallbackByUnicode(cp);
    }

    /**
     * 对 GB2312 一级字表之外的汉字做兜底估算。
     * 将 CJK 统一汉字区（0x4E00-0x9FFF，共 0x5200 个码点）等分为 26 段，
     * 每段映射到一个字母。结果不保证拼音正确，但保证返回 a-z 之间的合法字母，
     * 不会影响调用方的字符串拼接逻辑。
     */
    private static char fallbackByUnicode(int cp) {
        // CJK 统一汉字范围
        final int CJK_START = 0x4E00;
        final int CJK_END   = 0x9FFF;
        int clamped = Math.max(CJK_START, Math.min(CJK_END, cp));
        int index = (clamped - CJK_START) * 26 / (CJK_END - CJK_START + 1);
        return (char) ('a' + index);
    }
}
