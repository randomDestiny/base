package com.iscloud.common.cst;

import com.iscloud.common.helper.ListHelper;
import com.iscloud.common.helper.StringHelper;
import com.iscloud.common.utils.DateCompareUtils;
import com.iscloud.common.utils.DateFormatUtils;
import com.iscloud.common.vo.req.QueryParamsVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @Desc: 常量类
 * @Author ：HYbrid
 * @Date ：2021/4/26 11:36
 */
@SuppressWarnings("unused")
public interface BaseCst {

    String HEX_PRE = "0x";

    String STATIC = "static", FINAL = "final";
    String CLASSPATH = "classpath:", WEB_INF_CLS = "WEB-INF/classes/", PACKAGE = "package ";

    String BASE_PACKAGE = "com.indexsmart", BASE_PACKAGE_PATH = "com/indexsmart", ENTITY_PATH = "/**/entity/**/*.class";

    String SERVICE = "Service", MAPPER = "Mapper", CONTROLLER = "Controller";

    String CLOUD_REDIS_NAME = "cloud-redis-service";

    String LOCALHOST = "localhost", LOCAL = "127.0.0.1";

    static boolean isLocalhost(String s) {
        String str = StringUtils.isNotBlank(s) ? s.trim() : StringUtils.EMPTY;
        return LOCALHOST.equalsIgnoreCase(str) || LOCAL.equalsIgnoreCase(str);
    }

    /**
     * @Desc:   图表相关常量
     * @Author: HYbrid
     * @Date:   2022/6/14
     */
    interface Charts {
        // line=折线图,pie=饼图,sun=旭日图,bar=柱状图,line_push=实时推送折线图（二维数组）
        String CHART_LINE = "line",
                CHART_LINE_PUSH = "line_push",
                CHART_BAR = "bar",
                CHART_PIE = "pie",
                CHART_SUN = "sun";
    }

    /**
     * @Desc:   redis常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface RedisCst {
        String COMPANY_PRE = "c.", USER_PRE = "u.",
                DEV_PRE = "d.", DEV_POINT_PRE = "dp.", DEV_MODULE_PRE = "dm.";

        /**
         * @Desc:    存储company对象
         * @Author : HYbrid
         * @Date   : 2021/12/14 13:40
         * @Params : [companyId]
         * @Return : java.lang.String
         */
        static String companyKey(Long companyId) {
            if (companyId != null) {
                return COMPANY_PRE + companyId;
            }
            return null;
        }

        static String userKey(String u) {
            if (StringUtils.isNotBlank(u)) {
                return USER_PRE + u.trim();
            }
            return null;
        }

        /**
         * @Desc:    存储dev对象
         * @Author : HYbrid
         * @Date   : 2021/12/14 13:43
         * @Params : [companyId, devId]
         * @Return : java.lang.String
         */
        static String devKey(Long devId) {
            if (devId != null) {
                return DEV_PRE + devId;
            }
            return null;
        }

        /**
         * @Desc:    存储设备点表list
         * @Author : HYbrid
         * @Date   : 2021/12/15 11:16
         * @Params : [devId]
         * @Return : java.lang.String
         */
        static String devPointKey(Long devId) {
            String c = devKey(devId);
            if (c != null) {
                return DEV_POINT_PRE + devId;
            }
            return null;
        }

        /**
         * @Desc:    存储设备module对象
         * @Author : HYbrid
         * @Date   : 2021/12/15 11:56
         * @Params : [devId]
         * @Return : java.lang.String
         */
        static String devModuleKey(Long devId) {
            String c = devKey(devId);
            if (c != null) {
                return DEV_MODULE_PRE + devId;
            }
            return null;
        }
    }

    /**
     * @Desc:   表达式常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface PatternCst {
        Pattern P_AZ = Pattern.compile("[A-Z]");
        String LEFT_FILL_ZERO_FORMATTER = "%%0%sd",
                HEART_BEAT_FORMAT = "^\\[-?\\d{1,2}(,-?\\d{1,2})*]$",
                EMAIL_FORMAT = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*",
                IP_FORMAT = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}",
                BASE64_FORMAT = "^data:\\w+/\\w+;base64,(.+)$",
                LEFT_FILL_2_ZERO = "%02x",
                LEFT_FILL_2_ZERO_UP = "%02X",
                IS_CLASSPATH = "^classpath:",
                Q_LINE_2 = "\\\\",
                PLUS = "//+",
                IS_A_Z = "^[a-zA-Z]:",
                IS_UQ_LINE = "^/",
                POINT_W = "\\.\\w+$",
                POINT = "\\."
                        ;
    }

    /**
     * @Desc:   字符集常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface CharsetCst {
        String UTF8 = "UTF-8", GBK = "GBK", GB2312 = "GB2312";
    }

    /**
     * @Desc:   web交互常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface ResultCst {
        int STATUS_SUCCESS = 200, STATUS_NOT_FOUND = 404, STATUS_SERVER_EXCEPTION = 500, STATUS_BAD_REQUEST = 400;
        int ERROR_NONE = 0, // 正常状态
                ERROR_DB_OFFLINE = 95, // 数据库离线
                ERROR_LOGIN_EXPIRE = 96, // 登录超时
                ERROR_RUN_ERR = 97, // 运行错误
                ERROR_NO_PERMISSION = 98, // 没有权限
                ERROR_RUN_EXCEPTION = 99, // 运行异常
                ERROR_NO_LOGIN = 100, // 没有登录
                ERROR_FEIGN_EXCEPTION = 30, // feign调用异常
                ERROR_UNSUPPORTED = 1999 // 不支持的当前操作
                        ;
        String EMPTY_OBJECT = "{}", OBJ_PREFIX = "{", DATA = "data";
    }

    /**
     * @Desc:   微信常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface WechatCst {
        String ERR_CODE = "errcode", ERR_MSG = "errmsg", ACCESS_TOKEN = "access_token", OPENID = "openid";
        String DATA_COLOR = "#173177", DATA_COLOR_GREEN = "#71C671", DATA_COLOR_YELLOW = "#FFB90F", DATA_COLOR_RED = "#CD3333";
        String SSL = "SSL", SSL_SUN = "SunJSSE";
        String T_URL = "url", T_TO_USER = "touser", T_TEMPLATE_ID = "template_id", T_FIRST = "first", T_KEYWORD = "keyword",
                T_REMARK = "remark", T_DATA = "data", T_VALUE = "value", T_COLOR = "color";
        String ACCESS_TOKEN_URL_PAGE = "%s?appid=%s&secret=%s&code=%s&grant_type=authorization_code",
                ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token",
                TEMPLATE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
    }

    /**
     * @Desc:   web常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface WebCst {
        String PROD = "prod", DEV = "dev", TEST = "test";
        String POST = "POST", GET = "GET", HTTPS = "https", HTTP = "http", WS = "ws", WSS = "wss";
        String RES_STR = "response", STS_CODE = "statusCode", LOCATION_STR = "Location", COOKIES_STR = "cookies",
                ORIGIN = "Origin", OPTIONS = "OPTIONS", P3P = "P3P", CONTENT_ENCODING = "content-encoding", CONTENT_ENCODING_GZIP = "gzip",
                P3P_HEADER = "CP=NON DSP COR CURa ADMa DEVa TAIa PSAa PSDa IVAa IVDa CONa HISa TELa OTPa OUR UNRa IND UNI COM NAV INT DEM CNT PRE LOC",
                SN = "sn", SN_1 = "1", OPENID = "openid", COMPANY_ID = "companyId", SKIP_CHK_SN = "skipChkSN",
                USERNAME = "username", PASSWORD = "password", TOKEN = "token", STATUS = "status", MESSAGE = "message", ERROR_CODE = "errorCode",
                RES_FORMAT_ABLE = "resFormatAble", RES_FORMAT_ABLE_VAL = Boolean.FALSE.toString(),
                SUBSCRIPTION_PATH = "/subscription",
                DEFAULT_PSWD = "e10adc3949ba59abbe56e057f20f883e",
                ROOT = "hybrid",
                ROOT_PSWD = "71a5fa305a8c0b343e237f1787a4bd46",
                ROOT_TOKEN = "71a5fa305a8c0b343e237f1787a4bd46";
        long SIZE_MAX_LEN = 102400 * 1024, SIZE_MIN_LEN = 0;
        String SIZE_MAX_INFO = "100Mb", SIZE_MIN_INFO = "0Mb";
        String WS_MODULE_CONNECTION = "connection";
        String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
        String HEAD_ORIGIN = "Access-Control-Allow-Origin", REQ_HEADERS = "Access-Control-Request-Headers", DISP = "Content-Disposition",
                HEAD_HEADERS = "Access-Control-Allow-Headers", ALLOW_METHODS = "GET,POST,PUT,DELETE,PATCH,OPTIONS",
                EXPOSE_HEADERS = "Access-Control-Expose-Headers", ALLOW_CRED = "Access-Control-Allow-Credentials",
                HEAD_METHODS = "Access-Control-Allow-Methods";

    }

    /**
     * @Desc:   modbus常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface ModbusCst {
        int BYTE_BASE = 66, BYTE_BASE_2G = 54, BUFFER_SIZE = 4096, HEART_TIMEOUT = 58, TCP_HEAD_START = 9;
        long TIME_OUT = 100000000,
                HEART_TIME = 55000,
                MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1),
                SERIAL_PORT_READ_TIME_OUT = 5000
        ;
        String MODE_NETTY = "netty", MODE_SOCKET = "socket";
        String HEART_STR = "IS4G", HEART_BEAT_DEFAULT = "[73,83,52,71]";
        byte[] HEART = {73, 83, 52, 71}, TCP_HEAD = {1, 93, 0, 0, 0, 6};
    }

    /**
     * @Desc:   字符常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface SymbolCst {
        String POINT = ".", POINT_SUFFIX = "\\.", SPACE = " ", COLON = ":", COLON_ZH = "：", COMMA = ",", COMMA_ZH = "，",
                OBLIQUE_LINE = "/", LINE = "-", UNDER_LINE = "_", LINE_LINE = "--", VERTICAL_LINE = "|", QUOTATION = "'",
                NEW_LINE = "\n", NEW_LINE_2 = "\r\n", NEW_LINE_HTML = "<br/>", LEFT_PARENTHESIS = "(", RIGHT_PARENTHESIS = ")",
                EXCLAMATION = "!", EXCLAMATION_ZH = "！", QUESTION_MARK = "?", QUESTION_MARK_ZH = "？", SEMICOLON = ";",
                DOUBLE_QUOTATION = "\"", TAB = "    ", SQU_LEFT = "[", SQU_RIGHT = "]",
                AND = "&", EQ = "=", STAR = "*";
    }

    /**
     * @Desc:   文件类型常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface FileTypeCst {
        String JPEG = "jpeg", BMP = "bmp", JPG = "jpg", PNG = "png", TIF = "tif", GIF = "gif", PCX = "pcx", TGA = "tga",
                EXIF = "exif", FPX = "fpx", SVG = "svg", PSD = "psd", CDR = "cdr", PCD = "pcd", DXF = "dxf", UFO = "ufo",
                EPS = "eps", AI = "ai", RAW = "raw", WMF = "WMF", WEBP = "webp";
        String JAVA = "java", CLASS = "class", JAVA_SUF = ".java", CLASS_SUF = ".class";
        String FILE = "file";
        String[] IMG_TYPE = {JPEG, BMP, JPG, PNG, TIF, GIF, PCX, TGA, EXIF, FPX, SVG, PSD, CDR, PCD, DXF, UFO, AI, RAW, WMF, WEBP, EPS};
    }

    /**
     * @Desc:   时间类型常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface TimeTypeCst {

        long MS_1_SEC = 1000, MS_1_MIN = 60000, MS_1_HOUR = 3600000;
        // TIMESTAMP 时间戳
        String HOUR = "HH", YEAR = "yyyy", MONTH = "MM", MINUTE = "mm", SECOND = "ss", DAY = "dd", TIMESTAMP = "long", WEEK = "e";
        String SQL_DATE_FORMAT = "DATE_FORMAT(%s, '%s')", SQL_GROUP_DATE_FORMAT = " group by DATE_FORMAT(%s, '%s') %s",
                SQL_SELECT_DATE_FORMAT = " DATE_FORMAT(%s, '%s') %s";
        String MYSQL_HOUR = "HOUR", MYSQL_MICROSECOND = "MICROSECOND", MYSQL_SECOND = "SECOND", MYSQL_MINUTE = "MINUTE",
                MYSQL_DAY = "DAY", MYSQL_WEEK = "WEEK", MYSQL_MONTH = "MONTH", MYSQL_QUARTER = "QUARTER", MYSQL_YEAR = "YEAR";

        /**
         * @Desc:   根据时间类型初始化起止时间
         * @Params: [t]
         * @Return: void
         * @Author: HYbrid
         * @Date:   2022/6/15
         */
        static void initTimeType(QueryParamsVO t) {
            if (t != null && StringUtils.isAnyBlank(t.getStartTime(), t.getEndTime())) {
                Date d = new Date();
                String startTime = null, endTime = null;
                if (BaseCst.TimeTypeCst.WEEK.equals(t.getTimeType())) {
                    startTime = DateFormatUtils.formatDefault(DateCompareUtils.week1st(d));
                    endTime = DateFormatUtils.formatDefault(DateCompareUtils.weekend(d));
                } else if (BaseCst.TimeTypeCst.MONTH.equals(t.getTimeType())) {
                    startTime = DateFormatUtils.formatDefault(DateCompareUtils.yearFirstDay(d));
                    endTime = DateFormatUtils.formatDefault(DateCompareUtils.yearLastDay(d));
                } else if (BaseCst.TimeTypeCst.DAY.equals(t.getTimeType())) {
                    startTime = DateFormatUtils.formatDefault(DateCompareUtils.monthFirstDay(d));
                    endTime = DateFormatUtils.formatDefault(DateCompareUtils.monthLastDay(d));
                } else if (BaseCst.TimeTypeCst.YEAR.equals(t.getTimeType())) {
                    startTime = DateCompareUtils.addDate2Str(d, Calendar.YEAR, -10);
                    endTime = DateCompareUtils.lastSecondWithToday2Str(d);
                } else if (BaseCst.TimeTypeCst.HOUR.equals(t.getTimeType())) {
                    startTime = DateCompareUtils.firstSecondWithToday2Str(d);
                    endTime = DateCompareUtils.lastSecondWithToday2Str(d);
                }
                t.setStartTime(startTime);
                t.setEndTime(endTime);
            }
        }

        /**
         * @Desc:   timestamp=时间戳，precision=Calendar类中的精度
         * @Params: [timestamp, precision]
         * @Return: long
         * @Author: HYbrid
         * @Date:   2022/6/22
         */
        static long convertBy(long timestamp, int precision) {
            if (precision == Calendar.YEAR) {
                return timestamp / MS_1_HOUR / 24 / 365;
            } else if (precision == Calendar.MONTH) {
                return timestamp / MS_1_HOUR / 24 / 30;
            } else if (precision == Calendar.DAY_OF_MONTH) {
                return timestamp / MS_1_HOUR / 24;
            } else if (precision == Calendar.HOUR_OF_DAY) {
                return timestamp / MS_1_HOUR;
            } else if (precision == Calendar.MINUTE) {
                return timestamp / MS_1_MIN;
            } else if (precision == Calendar.SECOND) {
                return timestamp / MS_1_SEC;
            } else if (precision == Calendar.MILLISECOND) {
                return timestamp;
            } else {
                return 0;
            }
        }

        static String pattern(String timeType) {
            String[] s = DateFormatUtils.DateFormatter.PATTERN_19.split(timeType);
            if (s.length > 1) {
                return s[0] + timeType;
            }
            return YEAR;
        }

        static String sqlDateFormat(String col, String timeType) {
            return String.format(SQL_DATE_FORMAT, col, sqlDateFormat(timeType));
        }

        static String groupDateFormat(String col, String timeType) {
            return String.format(SQL_GROUP_DATE_FORMAT, col, sqlDateFormat(timeType), col);
        }

        static String sqlSelectDateFormat(String col, String timeType) {
            return String.format(SQL_SELECT_DATE_FORMAT, col, sqlDateFormat(timeType), col);
        }

        static String sqlDateFormat(String timeType) {
            if (HOUR.equalsIgnoreCase(timeType)) {
                return "%Y-%m-%e %H";
            } else if (YEAR.equalsIgnoreCase(timeType)) {
                return "%Y";
            } else if (MONTH.equals(timeType)) {
                return "%Y-%m";
            } else if (MINUTE.equals(timeType)) {
                return "%Y-%m-%e %H:%i";
            } else if (SECOND.equalsIgnoreCase(timeType)) {
                return "%Y-%m-%e %H:%i:%s";
            } else if (DAY.equalsIgnoreCase(timeType)) {
                return "%Y-%m-%e";
            } else if (TIMESTAMP.equalsIgnoreCase(timeType)) {
                return "%f";
            } else if (WEEK.equalsIgnoreCase(timeType)) {
                return "%W";
            }
            return "%W";
        }

        static String sqlTimeType(String timeType) {
            if (HOUR.equalsIgnoreCase(timeType)) {
                return MYSQL_HOUR;
            } else if (YEAR.equalsIgnoreCase(timeType)) {
                return MYSQL_YEAR;
            } else if (MONTH.equals(timeType)) {
                return MYSQL_MONTH;
            } else if (MINUTE.equals(timeType)) {
                return MYSQL_MINUTE;
            } else if (SECOND.equalsIgnoreCase(timeType)) {
                return MYSQL_SECOND;
            } else if (DAY.equalsIgnoreCase(timeType)) {
                return MYSQL_DAY;
            } else if (TIMESTAMP.equalsIgnoreCase(timeType)) {
                return MYSQL_MICROSECOND;
            } else if (WEEK.equalsIgnoreCase(timeType)) {
                return MYSQL_WEEK;
            }
            return MYSQL_MINUTE;
        }

        static String sqlTimeType(String timeType, boolean forward) {
            return sqlTimeType(timeTypeTransfer(timeType, forward));
        }

        /**
         * @Desc:   时间类型转换，向前移位还是向后移位，例：HH向前为dd;HH向后为mm
         * @Params: [timeType, forward]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/9
         */
        static String timeTypeTransfer(String timeType, boolean forward) {
            if (HOUR.equalsIgnoreCase(timeType)) {
                return forward ? MONTH : MINUTE;
            } else if (YEAR.equalsIgnoreCase(timeType)) {
                return forward ? YEAR : MONTH;
            } else if (MONTH.equals(timeType)) {
                return forward ? YEAR : DAY;
            } else if (MINUTE.equals(timeType)) {
                return forward ? HOUR : SECOND;
            } else if (SECOND.equalsIgnoreCase(timeType)) {
                return forward ? MINUTE : TIMESTAMP;
            } else if (DAY.equalsIgnoreCase(timeType)) {
                return forward ? MONTH : HOUR;
            } else if (TIMESTAMP.equalsIgnoreCase(timeType)) {
                return forward ? SECOND : TIMESTAMP;
            } else if (WEEK.equalsIgnoreCase(timeType)) {
                return forward ? MONTH : DAY;
            }
            return MINUTE;
        }

        /**
         * @Desc:   时间类型转换，向前移位还是向后移位，例：HH向前为24;HH向后为60
         * @Params: [timeType, forward]
         * @Return: java.math.BigDecimal
         * @Author: HYbrid
         * @Date:   2022/5/9
         */
        static BigDecimal unitTransfer(String timeType, boolean forward) {
            if (HOUR.equalsIgnoreCase(timeType)) {
                return forward ? BigDecimal.valueOf(24) : BigDecimal.valueOf(60);
            } else if (YEAR.equalsIgnoreCase(timeType)) {
                return forward ? BigDecimal.ONE : BigDecimal.valueOf(12);
            } else if (MONTH.equals(timeType)) {
                // 小时转月的时候会丢失精度，因为一个月可能会有28-31天
                return forward ? BigDecimal.valueOf(12) : BigDecimal.valueOf(30);
            } else if (MINUTE.equals(timeType)) {
                return BigDecimal.valueOf(60);
            } else if (SECOND.equalsIgnoreCase(timeType)) {
                return forward ? BigDecimal.valueOf(60) : BigDecimal.valueOf(1000);
            } else if (DAY.equalsIgnoreCase(timeType)) {
                // 小时转月的时候会丢失精度，因为一个月可能会有28-31天
                return forward ? BigDecimal.valueOf(30) : BigDecimal.valueOf(24);
            } else if (TIMESTAMP.equalsIgnoreCase(timeType)) {
                return forward ? BigDecimal.valueOf(1000) : BigDecimal.ONE;
            } else if (WEEK.equalsIgnoreCase(timeType)) {
                return forward ? BigDecimal.valueOf(4) : BigDecimal.valueOf(7);
            }
            return BigDecimal.valueOf(60);
        }
    }

    /**
     * @Desc:   系统配置常量
     * @Author: HYbrid
     * @Date:   2022/2/28
     */
    interface SystemConfigCst {
        String ATTACHMENT_CATEGORY = "attachment_category",
                PROFESSION = "profession",
                DEFAULT_SETTINGS = "defaultSettings",
                SYSTEM_SETTING = "systemSetting",
                SYSTEM_URL = "systemUrl",
                YIN_SHI_CAMERA = "yinshiCamera",
                MODBUS_REGISTER = "modbus_register",
                CHART_TYPE = "chart_type",
                COMPANY_ID = "companyId";
        String PROFESSION_SAM = "saleAfterManager", PROFESSION_SM = "systemManager", PROFESSION_PM = "projectManager",
                PROFESSION_MAINTAIN = "maintain";
        String YS_CAMERA_KEY = "appKey", YS_CAMERA_SECRET = "appSecret", YS_CAMERA_TOKEN_URL = "accessTokenUrl";
    }

    /**
     * @Desc:   mysql常量
     * @Author: HYbrid
     * @Date:   2022/2/25
     */
    interface MysqlCst {
        String NO = "NO", VIEW = "VIEW", TABLE = "BASE TABLE";
        String LIMIT_1 = " limit 1", LIMIT_FORMAT = " limit %s,%s";

        /**
         * @Desc:   格式化Limit语句
         * @Params: [n, s]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/6/29
         */
        static String limitFormat(int n, int s) {
            return String.format(LIMIT_FORMAT, (n - 1) * s, s);
        }

        static boolean isNotNull(String isNullAble) {
            return NO.equalsIgnoreCase(isNullAble);
        }

        /**
         * @Desc:   根据spring.datasource.url信息返回数据库名
         * @Params: [url]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/2/25
         */
        static String schema(String url) {
            if (StringUtils.isNotBlank(url)) {
                String[] s = url.substring(0, url.indexOf(SymbolCst.QUESTION_MARK)).split(SymbolCst.OBLIQUE_LINE);
                return s[s.length -1];
            }
            return StringUtils.EMPTY;
        }
    }

    /**
     * @Desc:   mysql数据类型常量
     * @Author: HYbrid
     * @Date:   2022/2/25
     */
    interface MysqlDataTypeCst {
        String BIGINT = "bigint",
                INT = "int",
                TINYINT = "tinyint",
                DECIMAL = "decimal",
                JSON = "json",
                VARCHAR = "varchar",
                DATE = "date",
                DATETIME = "datetime",
                TIMESTAMP = "timestamp",
                LONGTEXT = "longtext"
                        ;
    }

    /**
     * @Desc:   java数据类型常量
     * @Author: HYbrid
     * @Date:   2022/2/25
     */
    interface JavaDataTypeCst {
        String STR = "String",
                LONG = "Long",
                INT = "Integer",
                OBJ = "Object",
                BIG_DECIMAL = "BigDecimal",
                BOOL = "Boolean"
                        ;
    }

    /**
     * @Desc:   linux常量
     * @Author: HYbrid
     * @Date:   2022/5/30
     */
    interface LinuxCst {
        int LOGIN_FAIL_ERR = 1999;
        long TIMEOUT = 60000;

        String REQ_PTY = "bash";
        String CMD_EXIT = "exit",
                CMD_MD5SUM = "md5sum %s | awk '{ print $1 }'",
                CMD_FILE_SIZE = "ll | grep %s | awk '{ print $5 }'",
                CMD_WGET = "wget %s",
                CMD_CD = "cd %s",
                CMD_RENAME = "mv %s %s",
                CMD_RENAME_BATCH = "rename %s %s %s";

        /**
         * @Desc:   生成登录失败信息
         * @Params: [ip, username]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String loginFail(String ip, String username) {
            return String.format("用户名[%s]ssh登录linux[%s]失败！", username, ip);
        }

        /**
         * @Desc:   获取文件的md5校验信息
         * @Params: [path]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String md5sum(String path) {
            return String.format(CMD_MD5SUM, path);
        }

        /**
         * @Desc:   从网络获取资源
         * @Params: [url]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String wget(String url) {
            return String.format(CMD_WGET, url);
        }

        /**
         * @Desc:   打开文件夹
         * @Params: [path]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String openFold(String path) {
            return String.format(CMD_CD, path);
        }

        /**
         * @Desc:   获取文件大小，单位byte，参数为相对路径
         * @Params: [path]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/6/1
         */
        static String getFileSize(String path) {
            return String.format(CMD_FILE_SIZE, path);
        }

        /**
         * @Desc:   带绝对路径的文件名，如果不带路径就是重命名当前文件夹路径下的文件
         * @Params: [absolutePathSrc, absolutePathTarget]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String renameFile(String absolutePathSrc, String absolutePathTarget) {
            return String.format(CMD_RENAME, absolutePathSrc, absolutePathTarget);
        }

        /**
         * @Desc:   批量文件重命名
         * @Params: [str, replace, path]
         * @Return: java.lang.String
         * @Author: HYbrid
         * @Date:   2022/5/30
         */
        static String renameFileBatch(String str, String replace, String... path) {
            Set<String> pathSet = ListHelper.trim2Set(path);
            if (CollectionUtils.isNotEmpty(pathSet)) {
                String append = StringHelper.append(pathSet, SymbolCst.SPACE, true);
                return String.format(CMD_RENAME_BATCH, str, replace, append);
            }
            return null;
        }
    }

}
