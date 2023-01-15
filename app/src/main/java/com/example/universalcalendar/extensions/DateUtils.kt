@file:Suppress("DEPRECATED_IDENTITY_EQUALS")

package com.example.universalcalendar.extensions

import com.example.universalcalendar.common.Constant
import com.example.universalcalendar.common.Strings.EMPTY
import com.example.universalcalendar.model.HourGood
import java.text.Normalizer
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

object DateUtils {

    const val DATE_LOCALE_FORMAT = "yyyy-MM-dd"
    const val DOB_FORMAT = "dd/MM/YYYY"
    const val DATE_LOCALE_FORMAT_1 = "yyyyMd"
    const val DATE_LOCALE_FORMAT_2 = "yyyyMMdd"
    const val DATE_EVENT_REGISTER = "yyyyMMddHHmm"
    const val DATE_EVENT_NOTIFY = "HH:mm dd/MM/yyyy"
    const val TIME_EVENT_PARSE = "yyyyMdHm"
    const val DATE_EVENT_REGISTER_2 = "HH:mm"
    const val DOB_USER_FORMAT = "ddMMyyyy"
    const val WEEK_DAY_FORMAT = "EEEE"
    const val CONST_CAN_1 = 12
    const val CONST_CAN_10 = 9
    const val CONST_CAN_2 = 1
    const val CONST_CAN_3 = 2
    const val CONST_CAN_4 = 3
    const val CONST_CAN_5 = 4
    const val CONST_CAN_6 = 5
    const val CONST_CAN_7 = 6
    const val CONST_CAN_8 = 7
    const val CONST_CAN_9 = 8
    const val CONST_CHI_1 = 12
    const val CONST_CHI_10 = 9
    const val CONST_CHI_11 = 10
    const val CONST_CHI_12 = 11
    const val CONST_CHI_2 = 1
    const val CONST_CHI_3 = 2
    const val CONST_CHI_4 = 3
    const val CONST_CHI_5 = 4
    const val CONST_CHI_6 = 5
    const val CONST_CHI_7 = 6
    const val CONST_CHI_8 = 7
    const val CONST_CHI_9 = 8

    fun convertStringToDate(format: String, dateStr: String?): Date? {
        if (dateStr.isNullOrEmpty())
            return null
        return try {
            SimpleDateFormat(format, Locale.getDefault()).parse(dateStr)
        } catch (e: ParseException) {
//            Log.e("ConvertDateToString-ParseException: " + e.message)
            null
        }
    }

    fun convertDateToString(date: Date?, format: String): String? {
        if (date == null) return null
        return try {
            SimpleDateFormat(format, Locale.getDefault()).format(date)
        } catch (e: ParseException) {
//            Log.e("ConvertDateToString-ParseException: " + e.message)
            null
        }
    }

    private const val PI = Math.PI
    private const val TAG = "LunarCoreHelper"

    /**
     * In China, Vietnam and other East Asian countries,
     * we use the sexagenary cycle, also known as the Stems-and-Branches.
     * It appears as a means of recording days in the first Chinese written texts.
     * We have 10 Heavenly Stems and 12 Earthly Branches which make 60 Stem-Branch pairs.
     * From those pairs, we can "guess" which day is good, which day is not, based on some traditional rules.
     * For more info: https://en.wikipedia.org/wiki/Sexagenary_cycle
     *
     * Yin Yang name will be added later
     */
    // 10 Heavenly Stems
    // Vietnamese Heavenly Stems (or "Thiên Can")
    private val calendarInstance = Calendar.getInstance()
    private val ICON_CHI = arrayOf(
        "ico_giap_ty_chuot.png",
        "ico_giap_suu.png",
        "ico_giap_dan.png",
        "ico_giap_mao.png",
        "ico_giap_thin.png",
        "ico_giap_ty_ran.png",
        "ico_giap_ngo.png",
        "ico_giap_mui.png",
        "ico_giap_than.png",
        "ico_giap_dau.png",
        "ico_giap_tuat.png",
        "ico_giap_hoi.png"
    )
    private val CAN = arrayOf(
        "Giáp", "Ất", "Bính", "Đinh", "Mậu", "Kỷ", "Canh",
        "Tân", "Nhâm", "Quý"
    )

    // Chinese Heavenly Stems (or "天干")
    private val STEMS = arrayOf(
        "甲", "乙", "丙", "丁", "戊", "己", "庚",
        "辛", "壬", "癸"
    )

    // 12 Earthly Branches
    // Vietnamese Earthly Branches (or "Địa Chi")
    private val CHI = arrayOf(
        "Tý", "Sửu", "Dần", "Mão", "Thìn", "Tỵ", "Ngọ",
        "Mùi", "Thân", "Dậu", "Tuất", "Hợi"
    )

    private val CHIFORMONTH =
        arrayOf("Dần", "Mão", "Thìn", "Tỵ", "Ngọ", "Mùi", "Thân", "Dậu", "Tuất", "Hợi", "Tý", "Sửu")

    // Chinese Earthly Branches (or "地支")
    private val BRANCHES = arrayOf(
        "子", "丑", "寅", "卯", "辰", "巳", "午",
        "未", "申", "酉", "戌", "亥"
    )

    /**
     * Based on some traditional rules (again!), on each month,
     * we have 4 Chi(s)/Branches which is called "good", and 4 Chi(s)/Branches which called "bad".
     * The next 2-dimensional arrays are the lists of "good days" and "bad days".
     * We Vietnamese actually have other terms for those two. But we haven't found any English word for that.
     * So let's just call them "good days" and "bad days".
     * Reference will be added later.
     *
     * We use Chi/Branch with unaccented syllables to make it easy when comparing string.
     */
    private val goodDays = arrayOf(
        arrayOf("Tý", "sửu", "tỵ", "mui"),
        arrayOf("dần", "mão", "mùi", "dậu"),
        arrayOf("thìn", "tỵ", "dậu", "hợi"),
        arrayOf("ngọ", "mùi", "sửu", "dậu"),
        arrayOf("thân", "dậu", "sửu", "mão"),
        arrayOf("tuất", "hợi", "mão", "tỵ"),
        arrayOf("tý", "sửu", "tỵ", "mùi"),
        arrayOf("dần", "mão", "mùi", "dậu"),
        arrayOf("thìn", "tỵ", "dậu", "hợi"),
        arrayOf("ngọ", "mùi", "sửu", "dậu"),
        arrayOf("thân", "dậu", "sửu", "mão"),
        arrayOf("tuất", "hợi", "mão", "tỵ")
    )

    private val badDays = arrayOf(
        arrayOf("ngọ", "mão", "hợi", "dậu"),
        arrayOf("thân", "tỵ", "sửu", "hợi"),
        arrayOf("tuất", "mùi", "sửu", "hợi"),
        arrayOf("tý", "dậu", "tỵ", "mão"),
        arrayOf("dần", "hợi", "mùi", "tỵ"),
        arrayOf("thìn", "sửu", "dậu", "mùi"),
        arrayOf("ngọ", "mão", "hợi", "dậu"),
        arrayOf("thân", "tỵ", "sửu", "hợi"),
        arrayOf("tuất", "mùi", "sửu", "hợi"),
        arrayOf("tý", "dậu", "tỵ", "mão"),
        arrayOf("dần", "hợi", "mùi", "tỵ"),
        arrayOf("thìn", "sửu", "dậu", "mùi")
    )

    private val arrSunLongitude = arrayOf(
        0,
        15,
        30,
        45,
        60,
        75,
        90,
        105,
        120,
        135,
        150,
        165,
        180,
        195,
        210,
        225,
        240,
        255,
        270,
        285,
        300,
        315,
        330,
        345
    )

    private val GIO_HD = arrayOf(
        "110100101100",
        "001101001011",
        "110011010010",
        "101100110100",
        "001011001101",
        "010010110011"
    )
    private val arrMinorSolarName = arrayOf(
        "Xuân phân",
        "Thanh minh",
        "Cốc vũ",
        "Lập Hạ",
        "Tiểu mãn",
        "Mang chủng",
        "Hạ chí",
        "Tiểu thử",
        "Đại thử",
        "Lập thu",
        "Xử thử",
        "Bạch lộ",
        "Thu phân",
        "Hàn lộ",
        "Sương giáng",
        "Lập đông",
        "Tiểu tuyết",
        "Đại tuyết",
        "Đông chí",
        "Tiểu hàn",
        "Đại hàn",
        "Lập xuân",
        "Vũ thủy",
        "Kinh trập"
    )
    var arrListAdvice = arrayOf(
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Giác mộc Giao - Đặng Vũ: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>(Bình Tú) Tướng tinh con Giao Long, chủ trị ngày thứ 5</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Tạo tác mọi việc đều đặng vinh xương, tấn lợi. Hôn nhân cưới gả sanh con quý. Công danh khoa cử cao thăng, đỗ đạt. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Chôn cất hoạn nạn 3 năm. Sửa chữa hay xây đắp mộ phần ắt có người chết. Sanh con nhằm ngày có Sao Giác khó nuôi, nên lấy tên Sao mà đặt tên cho nó mới an toàn. Dùng tên sao của năm hay của tháng cũng được. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Sao Giác trúng ngày Dần là Đăng Viên được ngôi cao cả, mọi sự tốt đẹp. Sao Giác trúng ngày Ngọ là Phục Đoạn Sát: rất Kỵ chôn cất, xuất hành, thừa kế, chia lãnh gia tài, khởi công lò nhuộm lò gốm. Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. Sao Giác trúng ngày Sóc là Diệt Một Nhật: Đại Kỵ đi thuyền, và cũng chẳng nên làm rượu, lập lò gốm lò nhuộm, vào làm hành chánh, thừa kế. </font>",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Cang kim Long - Ngô Hán: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con Rồng , chủ trị ngày thứ 6</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Cắt may áo màn (sẽ có lộc ăn). </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Chôn cất bị Trùng tang. Cưới gả e phòng không giá lạnh. Tranh đấu kiện tụng lâm bại. Khởi dựng nhà cửa chết con đầu. 10 hoặc 100 ngày sau thì gặp họa, rồi lần lần tiêu hết ruộng đất, nếu làm quan bị cách chức. Sao Cang thuộc Thất Sát Tinh, sanh con nhằm ngày này ắt khó nuôi, nên lấy tên của Sao mà đặt cho nó thì yên lành</font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Sao Cang ở nhằm ngày Rằm là Diệt Một Nhật: Cử làm rượu, lập lò gốm lò nhuộm, vào làm hành chánh, thừa kế sự nghiệp, thứ nhất đi thuyền chẳng khỏi nguy hại ( vì Diệt Một có nghĩa là chìm mất ). Sao Cang tại Hợi, Mẹo, Mùi trăm việc đều tốt. Thứ nhất tại Mùi. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Đê thổ Lạc - Giả Phục: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con Lạc Đà, chủ trị ngày thứ 7</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Sao Đê Đại Hung , không cò việc chi hợp với nó </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công xây dựng, chôn cất, cưới gả, xuất hành kỵ nhất là đường thủy, sanh con chẳng phải điềm lành nên làm Âm Đức cho nó. Đó chỉ là các việc Đại Kỵ, các việc khác vẫn kiêng cử. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Thân, Tý, Thìn trăm việc đều tốt, nhưng Thìn là tốt hơn hết vì Sao Đê Đăng Viên tại Thìn. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Phòng nhật Thố - Cảnh Yêm: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con Thỏ , chủ trị ngày Chủ nhật</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác mọi việc đều tốt , thứ nhất là xây dựng nhà , chôn cất , cưới gả , xuất hành , đi thuyền , mưu sự , chặt cỏ phá đất , cắt áo. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Sao Phòng là Đại Kiết Tinh, không kỵ việc chi cả. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Đinh Sửu và Tân Sửu đều tốt, tại Dậu càng tốt hơn, vì Sao Phòng Đăng Viên tại Dậu. Trong 6 ngày Kỷ Tỵ, Đinh Tỵ, Kỷ Dậu, Quý Dậu, Đinh Sửu, Tân Sửu thì Sao Phòng vẫn tốt với các việc khác, ngoại trừ chôn cất là rất kỵ. Sao Phòng nhằm ngày Tỵ là Phục Đoạn Sát: chẳng nên chôn cất, xuất hành, các vụ thừa kế, chia lãnh gia tài, khởi công làm lò nhuộm lò gốm. Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Tâm nguyệt Hồ - Khấu Tuân: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung tú ) Tướng tinh con chồn, chủ trị ngày thứ 2</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Tạo tác việc chi cũng không hợp với Hung tú này. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công tạo tác việc chi cũng không khỏi hại, thứ nhất là xây cất, cưới gả, chôn cất, đóng giường, lót giường, tranh tụng. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Ngày Dần Sao Tâm Đăng Viên, có thể dùng các việc nhỏ. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'> Vĩ hỏa Hổ - Sầm Bành: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con cọp, chủ trị ngày thứ 3</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Mọi việc đều tốt , tốt nhất là các vụ khởi tạo , chôn cất , cưới gả , xây cất , trổ cửa , đào ao giếng , khai mương rạch , các vụ thủy lợi , khai trương , chặt cỏ phá đất. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đóng giường , lót giường, đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Hợi, Mẹo, Mùi Kỵ chôn cất. Tại Mùi là vị trí Hãm Địa của Sao Vỹ. Tại Kỷ Mẹo rất Hung, còn các ngày Mẹo khác có thể tạm dùng được. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Cơ thủy Báo - Phùng Dị: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con Beo , chủ trị ngày thứ 4</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi tạo trăm việc đều tốt, tốt nhất là chôn cất, tu bổ mồ mã, trổ cửa, khai trương, xuất hành, các vụ thủy lợi ( như tháo nước, đào kinh, khai thông mương rảnh. . . ). </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đóng giường, lót giường, đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Thân, Tý, Thìn trăm việc kỵ, duy tại Tý có thể tạm dùng. Ngày Thìn Sao Cơ Đăng Viên lẽ ra rất tốt nhưng lại phạm Phục Đoạn. Phạm Phục Đoạn thì kỵ chôn cất, xuất hành, các vụ thừa kế, chia lãnh gia tài, khởi công làm lò nhuộm lò gốm ; Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Đẩu mộc Giải - Tống Hữu: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con cua , chủ trị ngày thứ 5</font></em>. <br><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi tạo trăm việc đều tốt, tốt nhất là xây đắp hay sửa chữa phần mộ, trổ cửa, tháo nước, các vụ thủy lợi, chặt cỏ phá đất, may cắt áo mão, kinh doanh, giao dịch, mưu cầu công danh. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Rất kỵ đi thuyền. Con mới sanh đặt tên nó là Đẩu, Giải, Trại hoặc lấy tên Sao của năm hay tháng hiện tại mà đặt tên cho nó dễ nuôi. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Tỵ mất sức. Tại Dậu tốt. Ngày Sửu Đăng Viên rất tốt nhưng lại phạm Phục Đoạn. Phạm Phục Đoạn thì kỵ chôn cất, xuất hành, thừa kế, chia lãnh gia tài, khởi công làm lò nhuộm lò gốm ; Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Ngưu kim Ngưu - Sái Tuân: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con trâu , chủ trị ngày thứ 6</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Đi thuyền, cắt may áo mão. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công tạo tác việc chi cũng hung hại. Nhất là xây cất nhà, dựng trại, cưới gả, trổ cửa, làm thủy lợi, nuôi tằm, gieo cấy, khai khẩn, khai trương, xuất hành đường bộ. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Ngày Ngọ Đăng Viên rất tốt. Ngày Tuất yên lành. Ngày Dần là Tuyệt Nhật, chớ động tác việc chi, riêng ngày Nhâm Dần dùng được. Trúng ngày 14 âm lịch là Diệt Một Sát, cử: làm rượu, lập lò nhuộm lò gốm, vào làm hành chánh, thừa kế sự nghiệp, kỵ nhất là đi thuyền chẳng khỏi rủi ro. Sao Ngưu là 1 trong Thất sát Tinh, sanh con khó nuôi, nên lấy tên Sao của năm, tháng hay ngày mà đặt tên cho trẻ và làm việc Âm Đức ngay trong tháng sanh nó mới mong nuôi khôn lớn được. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Nữ thổ Bức - Cảnh Đan: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con dơi , chủ trị ngày thứ 7</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Kết màn, may áo. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công tạo tác trăm việc đều có hại, hung hại nhất là trổ cửa, khơi đường tháo nước, chôn cất, đầu đơn kiện cáo. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Hợi Mẹo Mùi đều gọi là đường cùng. Ngày Quý Hợi cùng cực đúng mức vì là ngày chót của 60 Hoa giáp. Ngày Hợi tuy Sao Nữ Đăng Viên song cũng chẳng nên dùng. Ngày Mẹo là Phục Đoạn Sát, rất kỵ chôn cất, xuất hành, thừa kế sự nghiệp, chia lãnh gia tài, khởi công làm lò nhuộm lò gốm ; Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Hư nhật Thử - Cái Duyên: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con chuột , chủ trị ngày chủ nhật</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Hư có nghĩa là hư hoại, không có việc chi hợp với Sao Hư. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công tạo tác trăm việc đều không may, thứ nhất là xây cất nhà cửa, cưới gả, khai trương, trổ cửa, tháo nước, đào kinh rạch. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Gặp Thân, Tý, Thìn đều tốt, tại Thìn Đắc Địa tốt hơn hết. hợp với 6 ngày Giáp Tý, Canh Tý, Mậu Thân, Canh Thân, Bính Thìn, Mậu Thìn có thể động sự. Trừ ngày Mậu Thìn ra, còn 5 ngày kia kỵ chôn cất. Gặp ngày Tý thì Sao Hư Đăng Viên rất tốt, nhưng lại phạm Phục Đoạn Sát: Kỵ chôn cất, xuất hành, thừa kế, chia lãnh gia tài sự nghiệp, khởi công làm lò nhuộm lò gốm, Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. Gặp Huyền Nhật là những ngày 7, 8 , 22, 23 âm lịch thì Sao Hư phạm Diệt Một: Cử làm rượu, lập lò gốm lò nhuộm, vào làm hành chánh, thừa kế, thứ nhất là đi thuyền ắt chẳng khỏi rủi ro. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Nguy nguyệt Yến - Kiên Đàm: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Bình Tú ) Tướng tinh con chim én, chủ trị ngày thứ 2</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Chôn cất rất tốt, lót giường bình yên. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Dựng nhà, trổ cửa, gác đòn đông, tháo nước, đào mương rạch, đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Tỵ, Dậu, Sửu trăm việc đều tốt, tại Dậu tốt nhất. Ngày Sửu Sao Nguy Đăng Viên: tạo tác sự việc được quý hiển. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Thất hỏa Trư - Cảnh Thuần: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con heo , chủ trị ngày thứ 3</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công trăm việc đều tốt. Tốt nhất là xây cất nhà cửa, cưới gả, chôn cất, trổ cửa, tháo nước, các việc thủy lợi, đi thuyền, chặt cỏ phá đất. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Sao thất Đại Kiết không có việc chi phải cử. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Dần, Ngọ, Tuất nói chung đều tốt, ngày Ngọ Đăng viên rất hiển đạt. Ba ngày Bính Dần, Nhâm Dần, Giáp Ngọ rất nên xây dựng và chôn cất, song những ngày Dần khác không tốt. Vì sao Thất gặp ngày Dần là phạm Phục Đoạn Sát ( kiêng cữ như trên ). </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Bích thủy Du - Tang Cung: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con rái cá , chủ trị ngày thứ 4</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác việc chi cũng tốt. Tốt nhất là xây cất nhà, cưới gả, chôn cất, trổ cửa, dựng cửa, tháo nước, các vụ thuỷ lợi, chặt cỏ phá đất, cắt áo thêu áo, khai trương, xuất hành, làm việc thiện ắt Thiện quả tới mau hơn. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Sao Bích toàn kiết, không có việc chi phải kiêng cử. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Hợi Mẹo Mùi trăm việc kỵ , thứ nhất trong Mùa Đông. Riêng ngày Hợi Sao Bích Đăng Viên nhưng phạm Phục Đoạn Sát ( Kiêng cữ như trên ). </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Khuê mộc Lang - Mã Vũ: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Bình Tú ) Tướng tinh con chó sói, chủ trị ngày thứ 5</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Tạo dựng nhà phòng , nhập học , ra đi cầu công danh , cắt áo. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Chôn cất , khai trương , trổ cửa dựng cửa , khai thông đường nước , đào ao móc giếng , thưa kiện , đóng giường lót giường. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Sao Khuê là 1 trong Thất Sát Tinh, nếu đẻ con nhằm ngày này thì nên lấy tên Sao Khuê hay lấy tên Sao của năm tháng mà đặt cho trẻ dễ nuôi. Sao Khuê Hãm Địa tại Thân: Văn Khoa thất bại. Tại Ngọ là chỗ Tuyệt gặp Sanh, mưu sự đắc lợi, thứ nhất gặp Canh Ngọ. Tại Thìn tốt vừa vừa. Ngày Thân Sao Khuê Đăng Viên: Tiến thân danh. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Lâu kim Cẩu - Lưu Long: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con chó , chủ trị ngày thứ 6</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công mọi việc đều tốt . Tốt nhất là dựng cột, cất lầu, làm dàn gác, cưới gả, trổ cửa dựng cửa, tháo nước hay các vụ thủy lợi, cắt áo. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đóng giường, lót giường, đi đường thủy. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Ngày Dậu Đăng Viên : Tạo tác đại lợi. Tại Tỵ gọi là Nhập Trù rất tốt. Tại Sửu tốt vừa vừa. Gặp ngày cuối tháng thì Sao Lâu phạm Diệt Một: rất kỵ đi thuyền, cữ làm rượu, lập lò gốm lò nhuộm, vào làm hành chánh, thừa kế sự nghiệp. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Vị thổ Trĩ - Ô Thành: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con chim trĩ , củ trị ngày thứ 7</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác việc chi cũng lợi. Tốt nhất là xây cất, cưới gả, chôn cất, chặt cỏ phá đất, gieo trồng, lấy giống. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Sao Vị mất chí khí tại Dần, thứ nhất tại Mậu Dần, rất là Hung, chẳng nên cưới gả, xây cất nhà cửa. Tại Tuất Sao Vị Đăng Viên nên mưu cầu công danh, nhưng cũng phạm Phục Đoạn ( kiêng cữ như các mục trên ). </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Mão nhật Kê - Vương Lương: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con gà , chủ trị ngày chủ nhật</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Xây dựng , tạo tác. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Chôn Cất ( ĐẠI KỴ ), cưới gả, trổ cửa dựng cửa, khai ngòi phóng thủy, khai trương, xuất hành, đóng giường lót giường. Các việc khác cũng không hay. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Mùi mất chí khí. Tại Ất Mẹo và Đinh Mẹo tốt, Ngày Mẹo Đăng Viên cưới gả tốt, nhưng ngày Quý Mẹo tạo tác mất tiền của. hợp với 8 ngày: Ất Mẹo, Đinh Mẹo, Tân Mẹo, Ất Mùi, Đinh Mùi, Tân Mùi, Ất Hợi, Tân Hợi. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Tất nguyệt Ô - Trần Tuấn: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con quạ, chủ trị ngày thứ 2</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác việc chi cũng tốt. Tốt nhất là chôn cất, cưới gả, trổ cửa dựng cửa, đào kinh, tháo nước, khai mương, móc giếng, chặt cỏ phá đất. Những việc khác cũng tốt như làm ruộng, nuôi tằm, khai trương, xuất hành, nhập học. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Thân, Tý, Thìn đều tốt. Tại Thân hiệu là Nguyệt Quải Khôn Sơn, trăng treo đầu núi Tây Nam, rất là tốt. Lại thên Sao tất Đăng Viên ở ngày Thân, cưới gả và chôn cất là 2 điều ĐẠI KIẾT. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Truỷ hỏa Hầu - Phó Tuấn: Xấu</font><em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con khỉ, chủ trị ngày thứ 3</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Không có sự việc chi hợp với Sao Chủy. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác việc chi cũng không tốt. KỴ NHẤT là chôn cất và các vụ thuộc về chết chôn như sửa đắp mồ mả, làm sanh phần (làm mồ mã để sẵn), đóng thọ đường (đóng hòm để sẵn). </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại tị bị đoạt khí, Hung càng thêm hung. Tại dậu rất tốt, vì Sao Chủy Đăng Viên ở Dậu, khởi động thăng tiến. Nhưng cũng phạm Phục Đoạn Sát. Tại Sửu là Đắc Địa, ắt nên. Rất hợp với ngày Đinh sửu và Tân Sửu, tạo tác Đại Lợi, chôn cất Phú Quý song toàn. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Sâm thủy Viên - Đỗ Mậu: Tốt</font><em><font style='font-family:roboto;font-size:18px'>( Bình Tú ) Tướng tinh con vượn , chủ trị ngày thứ 4</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác nhiều việc tốt như : xây cất nhà, dựng cửa trổ cửa, nhập học, đi thuyền, làm thủy lợi, tháo nước đào mương. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Cưới gả, chôn cất, đóng giường lót giường, kết bạn. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Ngày Tuất Sao sâm Đăng Viên, nên phó nhậm, cầu công danh hiển hách. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Tỉnh mộc Hãn - Diêu Kỳ: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Bình Tú ) Tướng tinh con dê trừu, chủ trị ngày thứ 5</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Tạo tác nhiều việc tốt như xây cất, trổ cửa dựng cửa, mở thông đường nước, đào mương móc giếng, nhậm chức, nhập học, đi thuyền. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Chôn cất, tu bổ phần mộ, làm sanh phần, đóng thọ đường. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Hợi, Mẹo, Mùi trăm việc tốt. Tại Mùi là Nhập Miếu, khởi động vinh quang. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Quỷ kim Dương - Vương Phách: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con dê , chủ trị ngày thứ 6</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Chôn cất, chặt cỏ phá đất, cắt áo. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi tạo việc chi cũng hại. Hại nhất là xây cất nhà, cưới gả, trổ cửa dựng cửa, tháo nước, đào ao giếng, động đất, xây tường, dựng cột. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Ngày Tý Đăng Viên thừa kế tước phong tốt, phó nhiệm may mắn. Ngày Thân là Phục Đoạn Sát kỵ chôn cất, xuất hành, thừa kế, chia lãnh gia tài, khởi công lập lò gốm lò nhuộm; Nhưng nên dứt vú trẻ em, xây tường, lấp hang lỗ, làm cầu tiêu, kết dứt điều hung hại. Nhằm ngày 16 âm lịch là ngày Diệt Một kỵ làm rượu, lập lò gốm lò nhuộm, vào làm hành chánh, kỵ nhất đi thuyền. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Liễu thổ Chương - Nhậm Quang: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung tú ) Tướng tinh con gấu ngựa , chủ trị ngày thứ 7</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Không có việc chi hợp với Sao Liễu. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Khởi công tạo tác việc chi cũng hung hại. Hung hại nhất là chôn cất, xây đắp, trổ cửa dựng cửa, tháo nước, đào ao lũy, làm thủy lợi. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Ngọ trăm việc tốt. Tại Tỵ Đăng Viên: thừa kế và lên quan lãnh chức là 2 điều tốt nhất. Tại Dần, Tuất rất kỵ xây cất và chôn cất : Rất suy vi. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Tinh nhật Mã - Lý Trung: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Bình Tú ) Tướng tinh con ngựa , chủ trị ngày chủ nhật</font></em><br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Xây dựng phòng mới. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'> Chôn cất, cưới gả, mở thông đường nước. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Sao Tinh là 1 trong Thất Sát Tinh, nếu sanh con nhằm ngày này nên lấy tên Sao đặt tên cho trẻ để dễ nuôi, có thể lấy tên sao của năm, hay sao của tháng cũng được. Tại Dần Ngọ Tuất đều tốt, tại Ngọ là Nhập Miếu, tạo tác được tôn trọng. Tại Thân là Đăng Giá ( lên xe ): xây cất tốt mà chôn cất nguy. hợp với 7 ngày: Giáp Dần, Nhâm Dần, Giáp Ngọ, Bính Ngọ, Mậu Ngọ, Bính Tuất, Canh Tuất. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Trương nguyệt Lộc - Vạn Tu: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con nai , chủ trị ngày thứ 2</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác trăm việc tốt, tốt nhất là xây cất nhà, che mái dựng hiên, trổ cửa dựng cửa, cưới gả, chôn cất, làm ruộng, nuôi tằm, đặt táng kê gác, chặt cỏ phá đất, cắt áo, làm thuỷ lợi. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Sửa hoặc làm thuyền chèo, đẩy thuyền mới xuống nước. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Hợi, Mẹo, Mùi đều tốt. Tại Mùi Đăng viên rất tốt nhưng phạm Phục Đoạn. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Dực hỏa Xà - Bi Đồng: Xấu</font>. <em><font style='font-family:roboto;font-size:18px'>( Hung Tú ) Tướng tinh con rắn , chủ trị ngày thứ 3</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Cắt áo sẽ đước tiền tài. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'> Chôn cất, cưới gả, xây cất nhà, đặt táng kê gác, gác đòn dông, trổ cửa gắn cửa, các vụ thủy lợi. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Thân, Tý, Thìn mọi việc tốt. Tại Thìn Vượng Địa tốt hơn hết. Tại Tý Đăng Viên nên thừa kế sự nghiệp, lên quan lãnh chức. </font> ",
        "<font color='#0066CC' style='font-family:roboto;font-size:20px'>Chẩn thủy Dẫn - Lưu Trực: Tốt</font>. <em><font style='font-family:roboto;font-size:18px'>( Kiết Tú ) Tướng tinh con giun, chủ trị ngày thứ 4</font></em>. <br><font color='#006600' style='font-family:roboto;font-size:20px'>Nên làm : </font><font style='font-family:roboto;font-size:18px'> Khởi công tạo tác mọi việc tốt lành, tốt nhất là xây cất lầu gác, chôn cất, cưới gả. Các việc khác cũng tốt như dựng phòng, cất trại, xuất hành, chặt cỏ phá đất. </font><br><font color='#990000' style='font-family:roboto;font-size:20px'>Kiêng cữ : </font><font style='font-family:roboto;font-size:18px'>Đi thuyền. </font><br><font color='#999900' style='font-family:roboto;font-size:20px'>Ngoại lệ : </font><font style='font-family:roboto;font-size:18px'> Tại Tỵ Dậu Sửu đều tốt. Tại Sửu Vượng Địa, tạo tác thịnh vượng. Tại Tỵ Đăng Viên là ngôi tôn đại, mưu động ắt thành danh. "
    )
    var arrListAdvice2 = arrayOf(
        "<font style='font-family:roboto;font-size:18px'><br>Giác tinh tọa tác chủ vinh xương, <br>Ngoại tiến điền tài cập nữ lang, <br>Giá thú hôn nhân sinh quý tử, <br>Vănh nhân cập đệ kiến Quân vương. <br>Duy hữu táng mai bất khả dụng, <br>Tam niên chi hậu, chủ ôn đậu, <br>Khởi công tu trúc phần mộ địa, <br>Đường tiền lập kiến chủ nhân vong. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Cang tinh tạo tác Trưởng phòng đường, <br>Thập nhật chi trung chủ hữu ương, <br>Điền địa tiêu ma, quan thất chức, <br>Đầu quân định thị hổ lang thương. <br>Giá thú, hôn nhân dụng thử nhật, <br>Nhi tôn, Tân phụ chủ không phòng, <br>Mai táng nhược hoàn phùng thử nhật, <br>Đương thời tai họa, chủ trùng tang. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Đê tinh tạo tác chủ tai hung, <br>Phí tận điền viên, thương khố không, <br>Mai táng bất khả dụng thử nhật, <br>Huyền thằng, điếu khả, họa trùng trùng, <br>Nhược thị hôn nhân ly biệt tán, <br>Dạ chiêu lãng tử nhập phòng trung. <br>Hành thuyền tắc định tạo hướng một, <br>Cánh sinh lung ách, tử tôn cùng. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Phòng tinh tạo tác điền viên tiến, <br>Huyết tài ngưu mã biến sơn cương, <br>Cánh chiêu ngoại xứ điền trang trạch, <br>Vinh hoa cao quý, phúc thọ khang. <br>Mai táng nhược nhiên phùng thử nhật, <br>Cao quan tiến chức bái Quân vương. <br>Giá thú: Thường nga quy Nguyệt điện, <br>Tam niên bào tử chế triều đường. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Tâm tinh tạo tác đại vi hung, <br>Cánh tao hình tụng, ngục tù trung, <br>Ngỗ nghịch quan phi, điền trạch thoái, <br>Mai táng tốt bộc tử tương tòng. <br>Hôn nhân nhược thị phùng thử nhật, <br>Tử tử nhi vong tự mãn hung. <br>Tam niên chi nội liên tạo họa, <br>Sự sự giáo quân một thủy chung. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Vĩ tinh tạo tác đắc thiên ân, <br>Phú quý, vinh hoa, phúc thọ ninh, <br>Chiêu tài tiến bảo, tiến điền địa, <br>Hòa hợp hôn nhân, quý tử tôn. <br>Mai táng nhược năng y thử nhật, <br>Nam thanh, nữ chính, tử tôn hưng. <br>Khai môn, phóng thủy, chiêu điền địa, <br>Đại đại công hầu, viễn bá danh. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Cơ tinh tạo tác chủ cao cường, <br>Tuế tuế niên niên đại cát xương, <br>Mai táng, tu phần đại cát lợi, <br>Điền tàm, ngưu mã biến sơn cương. <br>Khai môn, phóng thủy chiêu tài cốc, <br>Khiếp mãn kim ngân, cốc mãn thương. <br>Phúc ấm cao quan gia lộc vị, <br>Lục thân phong lộc, phúc an khang. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Đẩu tinh tạo tác chủ chiêu tài, <br>Văn vũ quan viên vị đỉnh thai, <br>Điền trạch tiền tài thiên vạn tiến, <br>Phần doanh tu trúc, phú quý lai. <br>Khai môn, phóng thủy, chiêu ngưu mã, <br>Vượng tài nam nữ chủ hòa hài, <br>Ngộ thử cát tinh lai chiến hộ, <br>Thời chi phúc khánh, vĩnh vô tai. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Ngưu tinh tạo tác chủ tai nguy, <br>Cửu hoành tam tai bất khả thôi, <br>Gia trạch bất an, nhân khẩu thoái, <br>Điền tàm bất lợi, chủ nhân suy. <br>Giá thú, hôn nhân giai tự tổn, <br>Kim ngân tài cốc tiệm vô chi. <br>Nhược thị khai môn, tính phóng thủy, <br>Ngưu trư dương mã diệc thương bi. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Nữ tinh tạo tác tổn bà nương, <br>Huynh đệ tương hiềm tựa hổ lang, <br>Mai táng sinh tai phùng quỷ quái, <br>Điên tà tật bệnh cánh ôn hoàng. <br>Vi sự đáo quan, tài thất tán, <br>Tả lị lưu liên bất khả đương. <br>Khai môn, phóng thủy phùng thử nhật, <br>Toàn gia tán bại, chủ ly hương. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Hư tinh tạo tác chủ tai ương, <br>Nam nữ cô miên bất nhất song, <br>Nội loạn phong thanh vô lễ tiết, <br>Nhi tôn, tức phụ bạn nhân sàng, <br>Khai môn, phóng thủy chiêu tai họa, <br>Hổ giảo, xà thương cập tốt vong. <br>Tam tam ngũ ngũ liên niên bệnh, <br>Gia phá, nhân vong, bất khả đương. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Nguy tinh bât khả tạo cao đường, <br>Tự điếu, tao hình kiến huyết quang<br>Tam tuế hài nhi tao thủy ách, <br>Hậu sinh xuất ngoại bất hoàn lương. <br>Mai táng nhược hoàn phùng thử nhật, <br>Chu niên bách nhật ngọa cao sàng, <br>Khai môn, phóng thủy tạo hình trượng, <br>Tam niên ngũ tái diệc bi thương. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Thất tinh tạo tác tiến điền ngưu, <br>Nhi tôn đại đại cận quân hầu, <br>Phú quý vinh hoa thiên thượng chỉ, <br>Thọ như Bành tổ nhập thiên thu. <br>Khai môn, phóng thủy chiêu tài bạch, <br>Hòa hợp hôn nhân sinh quý nhi. <br>Mai táng nhược năng y thử nhật, <br>Môn đình hưng vượng, Phúc vô ưu!<br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Bích tinh tạo ác tiến trang điền<br>Ti tâm đại thục phúc thao thiên, <br>Nô tỳ tự lai, nhân khẩu tiến, <br>Khai môn, phóng thủy xuất anh hiền, <br>Mai táng chiêu tài, quan phẩm tiến, <br>Gia trung chủ sự lạc thao nhiên<br>Hôn nhân cát lợi sinh quý tử, <br>Tảo bá thanh danh khán tổ tiên. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Khuê tinh tạo tác đắc trinh tường, <br>Gia hạ vinh hòa đại cát xương, <br>Nhược thị táng mai âm tốt tử, <br>Đương niên định chủ lưỡng tam tang. <br>Khán khán vận kim, hình thương đáo, <br>Trùng trùng quan sự, chủ ôn hoàng. <br>Khai môn phóng thủy chiêu tai họa, <br>Tam niên lưỡng thứ tổn nhi lang. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Lâu tinh thụ trụ, khởi môn đình, <br>Tài vượng, gia hòa, sự sự hưng, <br>Ngoại cảnh, tiền tài bách nhật tiến, <br>Nhất gia huynh đệ bá thanh danh. <br>Hôn nhân tiến ích, sinh quý tử, <br>Ngọc bạch kim lang tương mãn doanh, <br>Phóng thủy, khai môn giai cát lợi, <br>Nam vinh, nữ quý, thọ khang ninh. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Vị tinh tạo tác sự như hà, <br>Phú quý, vinh hoa, hỷ khí đa, <br>Mai táng tiến lâm quan lộc vị, <br>Tam tai, cửu họa bất phùng tha. <br>Hôn nhân ngộ thử gia phú quý, <br>Phu phụ tề mi, vĩnh bảo hòa, <br>Tòng thử môn đình sinh cát khánh, <br>Nhi tôn đại đại bảo kim pha. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Mão tinh tạo tác tiến điền ngưu, <br>Mai táng quan tai bất đắc hưu, <br>Trùng tang nhị nhật, tam nhân tử, <br>Mại tận điền viên, bất năng lưu. <br>Khai môn, phóng thủy chiêu tai họa, <br>Tam tuế hài nhi bạch liễu đầu, <br>Hôn nhân bất khả phùng nhật thử, <br>Tử biệt sinh ly thật khả sầu. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Tất tinh tạo tác chủ quang tiền, <br>Mãi dắc điền viên hữu lật tiền<br>Mai táng thử nhâtj thiêm quan chức, <br>Điền tàm đại thực lai phong niên<br>Khai môn phóng thủy đa cát lật, <br>Hợp gia nhân khẩu đắc an nhiên, <br>Hôn nhân nhược năng phùng thử nhật, <br>Sinh đắc hài nhi phúc thọ toàn. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Truỷ tinh tạo tác hữu đồ hình, <br>Tam niên tất đinh chủ linh đinh, <br>Mai táng tốt tử đa do thử, <br>Thủ định Dần niên tiện sát nhân. <br>Tam tang bất chỉ giai do thử, <br>Nhất nhân dược độc nhị nhân thân. <br>Gia môn điền địa giai thoán bại, <br>Thương khố kim tiền hóa tác cần. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Sâm tinh tạo tác vượng nhân gia, <br>Văn tinh triều diệu, đại quang hoa, <br>Chỉ nhân tạo tác điền tài vượng, <br>Mai táng chiêu tật, táng hoàng sa. <br>Khai môn, phóng thủy gia quan chức, <br>Phòng phòng tôn tử kiến điền gia, <br>Hôn nhân hứa định tao hình khắc, <br>Nam nữ chiêu khai mộ lạc hoa. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Tỉnh tinh tạo tác vượng tàm điền, <br>Kim bảng đề danh đệ nhất tiên, <br>Mai táng, tu phòng kinh tốt tử, <br>Hốt phong tật nhập hoàng điên tuyền<br>Khai môn, phóng thủy chiêu tài bạch, <br>Ngưu mã trư dương vượng mạc cát, <br>Quả phụ điền đường lai nhập trạch, <br>Nhi tôn hưng vượng hữu dư tiền. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Quỷ tinh khởi tạo tất nhân vong, <br>Đường tiền bất kiến chủ nhân lang, <br>Mai táng thử nhật, quan lộc chí, <br>Nhi tôn đại đại cận quân vương. <br>Khai môn phóng thủy tu thương tử, <br>Hôn nhân phu thê bất cửu trường. <br>Tu thổ trúc tường thương sản nữ, <br>Thủ phù song nữ lệ uông uông. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Liễu tinh tạo tác chủ tao quan, <br>Trú dạ thâu nhàn bất tạm an, <br>Mai táng ôn hoàng đa bệnh tử, <br>Điền viên thoái tận, thủ cô hàn, <br>Khai môn phóng thủy chiêu lung hạt, <br>Yêu đà bối khúc tự cung loan<br>Cánh hữu bổng hình nghi cẩn thận, <br>Phụ nhân tùy khách tẩu bất hoàn. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Tinh tú nhật hảo tạo tân phòng, <br>Tiến chức gia quan cận Đế vương, <br>Bất khả mai táng tính phóng thủy, <br>Hung tinh lâm vị nữ nhân vong. <br>Sinh ly, tử biệt vô tâm luyến, <br>Tự yếu quy hưu biệt giá lang. <br>Khổng tử cửu khúc châu nan độ, <br>Phóng thủy, khai câu, thiên mệnh thương. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Trương tinh nhật hảo tạo long hiên, <br>Niên niên tiện kiến tiến trang điền, <br>Mai táng bất cửu thăng quan chức, <br>Đại đại vi quan cận Đế tiền, <br>Khai môn phóng thủy chiêu tài bạch, <br>Hôn nhân hòa hợp, phúc miên miên. <br>Điền tàm đại lợi, thương khố mãn, <br>Bách ban lợi ý, tự an nhiên. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Dực tinh bất lợi giá cao đường, <br>Tam niên nhị tái kiến ôn hoàng, <br>Mai táng nhược hoàn phùng thử nhật, <br>Tử tôn bất định tẩu tha hương. <br>Hôn nhân thử nhật nghi bất lợi, <br>Quy gia định thị bất tương đương. <br>Khai môn phóng thủy gia tu phá, <br>Thiếu nữ tham hoa luyến ngoại lang. <br></font>",
        "<font style='font-family:roboto;font-size:18px'><br>Chẩn tinh lâm thủy tạo long cung, <br>Đại đại vi quan thụ sắc phong, <br>Phú quý vinh hoa tăng phúc thọ, <br>Khố mãn thương doanh tự Xương: long. <br>Mai táng văn tinh lai chiếu trợ, <br>Trạch xá an ninh, bất kiến hung. <br>Cánh hữu vi quan, tiên đế sủng, <br>Hôn nhân long tử xuất long cung. <br></font>"
    )


    fun isGoodDay(chiDay: String, lunarMonth: Int): Boolean {
        val data = goodDays[lunarMonth - 1]
        val tmp = data.size
        for (i in 0 until tmp) {
            if (data[i].equals(chiDay, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun isBadDay(chiDay: String, lunarMonth: Int): Boolean {
        if (isGoodDay(chiDay, lunarMonth)) return false
        val data = badDays[lunarMonth - 1]
        val tmp = data.size
        for (aData in data) {
            if (aData.equals(chiDay, ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @return days between March 1st 1996 and the input date. Why March 1st 1996, see the "processDayLunar" method below.
     */
    private fun getDateDurationBetweenInputAndPivotDate(
        solarDay: Int,
        solarMonth: Int,
        solarYear: Int
    ): Int {
        val currentCalendar: GregorianCalendar = GregorianCalendar()
        currentCalendar[solarYear, solarMonth - 1, solarDay, 0, 0] = 0
        val checkCalendar: GregorianCalendar = GregorianCalendar()
        checkCalendar[1996, 2, 1, 0, 0] = 0
        return ((currentCalendar.timeInMillis / 1000L - checkCalendar
            .timeInMillis / 1000L) / (60 * 60 * 24)).toInt()
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @return Can-Chi (or Stem-Branch) number of the input date.
     * We make March 1st 1996 a "pivot" date, and start counting Can-Chi from that date.
     * You can see we set iCan = 3, iChi = 9 means the "pivot" day is Đinh Dậu (or 丁酉, or Yin Fire Rooster).
     * So yeah, it's not a special date. Just a "pivot", a starting point to count. You can choose another day, which has another iCan and iChi.
     */
    private fun processDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): IntArray {
        var iCan = 3
        var iChi = 9
        val numDays = getDateDurationBetweenInputAndPivotDate(solarDay, solarMonth, solarYear)
        //        Log.d(TAG, "Test: " + iCan + "/" + iChi + "/" + numDays);
        if (numDays < 0) {
            iCan = (iCan + numDays % 10 + 10) % 10
            iChi = (iChi + numDays % 12 + 12) % 12
        } else if (numDays > 0) {
            iCan = (iCan + numDays % 10) % 10
            iChi = (iChi + numDays % 12) % 12
        }
        //        Log.d(TAG, "Test2: " + iCan + "/" + iChi + "/" + numDays);
        return intArrayOf(iCan, iChi)
    }

    private fun getCan(str: String): Int {
        var i = 0
        while (true) {
            if (i >= CAN.size) {
                return -1
            }
            if (str == CAN[i]) {
                return i
            }
            ++i
        }
    }

    private fun getChi(str: String): Int {
        var i = 0
        while (true) {
            if (i >= CHI.size) {
                return -1
            }
            if (str == CHI[i]) {
                return i
            }
            ++i
        }
    }

    fun getChiDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        val tmp: IntArray =
            processDayLunar(solarDay, solarMonth, solarYear)
        return CHI[tmp[1]]
    }


    fun getCanChiDayLunar(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        return "${CAN[(jdn(solarDay, solarMonth, solarYear) + 9) % 10]} ${
            CHI[(jdn(
                solarDay,
                solarMonth,
                solarYear
            ) + 1) % 12]
        }"
    }

    private fun getDayCanChi(solarDay: Int, solarMonth: Int, solarYear: Int): Array<String> {
        val convertDay = convertDay(solarDay, solarMonth, solarYear)
        return arrayOf(
            CAN[(convertDay + 9) % 10],
            CHI[(convertDay + 1) % 12]
        )
    }

    fun getCanYearLunar(solarYear: Int): String {
        return CAN[(solarYear + 6) % 10]
    }

    fun getChiYearLunar(solarYear: Int): String {
        return CHI[(solarYear + 8) % 12]
    }

    fun getCanChiForMonth(solarMonth: Int, solarYear: Int): String {
        val canChiMonth =  ((solarYear * 12) + solarMonth + 3) % 10
        return "${CAN[canChiMonth]} ${CHIFORMONTH[solarMonth - 1]}"
    }

    /**
     *
     * @param s
     * @return words unaccented syllables, with an exception of "Đ/đ"
     */
    private fun unAccent(s: String): String {
        val temp: String = Normalizer.normalize(s, Normalizer.Form.NFD)
        val pattern: Pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
        return pattern.matcher(temp).replaceAll("").replace("Đ", "D").replace("đ", "d")
    }

    /**
     *
     * @param canchi
     * @return Can-Chi with unaccented syllables, with an exception of "Tý" and "Tỵ" which has the same result when unaccented.
     */
    private fun getUnAccentCanChi(canchi: String): String {
        var rs = unAccent(canchi)
        if (canchi.equals("tý", ignoreCase = true)) {
            rs += "s"
        } else if (canchi.equals("tỵ", ignoreCase = true)) {
            rs += "j"
        }
        return rs.lowercase(Locale.getDefault())
    }

    /**
     *
     * @param chiDay
     * @param lunarMonth
     * @return the string showing the input day is good or not
     * We use Can-Chi string with unaccented syllables to compare.
     * And yeah, "rateDay" is not a really good name for this method!
     */
    fun rateDay(chiDay: String, lunarMonth: Int): String? {
        var chiDay = chiDay
        chiDay = getUnAccentCanChi(chiDay)
        return if (isGoodDay(chiDay, lunarMonth)) {
            "Good"
        } else if (isBadDay(chiDay, lunarMonth)) {
            "Bad"
        } else {
            "Normal"
        }
    }


    /**
     *
     * @param dd
     * @param mm
     * @param yy
     * @return the number of days since 1 January 4713 BC (Julian calendar)
     */
    private fun jdFromDate(dd: Int, mm: Int, yy: Int): Int {
        val a = (14 - mm) / 12
        val y = yy + 4800 - a
        val m = mm + 12 * a - 3
        var jd = (dd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400
                - 32045)
        if (jd < 2299161) {
            jd = dd + (153 * m + 2) / 5 + 365 * y + y / 4 - 32083
        }
        // jd = jd - 1721425;
        return jd
    }

    /**
     * http://www.tondering.dk/claus/calendar.html Section: Is there a formula
     * for calculating the Julian day number?
     *
     * @param jd
     * - the number of days since 1 January 4713 BC (Julian calendar)
     * @return
     */
    private fun jdToDate(jd: Int): IntArray? {
        val a: Int
        val b: Int
        val c: Int
        if (jd > 2299160) { // After 5/10/1582, Gregorian calendar
            a = jd + 32044
            b = (4 * a + 3) / 146097
            c = a - b * 146097 / 4
        } else {
            b = 0
            c = jd + 32082
        }
        val d = (4 * c + 3) / 1461
        val e = c - 1461 * d / 4
        val m = (5 * e + 2) / 153
        val day = e - (153 * m + 2) / 5 + 1
        val month = m + 3 - 12 * (m / 10)
        val year = b * 100 + d - 4800 + m / 10
        return intArrayOf(day, month, year)
    }

    fun jdn(i: Int, i2: Int, i3: Int): Int {
        val intValue = Integer.valueOf((14 - i2) / 12).toInt()
        val i4 = i3 + 4800 - intValue
        return i + Integer.valueOf(((i2 + intValue * 12 - 3) * 153 + 2) / 5)
            .toInt() + i4 * 365 + Integer.valueOf(i4 / 4).toInt() - Integer.valueOf(i4 / 100)
            .toInt() + Integer.valueOf(i4 / 400).toInt() - 32045
    }

    /**
     * Solar longitude in degrees Algorithm from: Astronomical Algorithms, by
     * Jean Meeus, 1998
     *
     * @param jdn
     * - number of days since noon UTC on 1 January 4713 BC
     * @return
     */
    private fun SunLongitude(jdn: Double): Double {
        // return CC2K.sunLongitude(jdn);
        return SunLongitudeAA98(jdn)
    }

    private fun SunLongitudeAA98(jdn: Double): Double {
        val T = (jdn - 2415021.0) / 36525 // Time in Julian centuries from
        // 2000-01-01 12:00:00 GMT
        val T2 = T * T
        val dr = PI / 180 // degree to radian
        val M = 357.52910 + 35999.05030 * T - 0.0001559 * T2 - (0.00000048
                * T * T2) // mean anomaly, degree
        val L0 = 280.46645 + 36000.76983 * T + 0.0003032 * T2 // mean
        // longitude,
        // degree
        var DL = ((1.914600 - 0.004817 * T - 0.000014 * T2)
                * Math.sin(dr * M))
        DL += (0.019993 - 0.000101 * T) * Math.sin(dr * 2 * M) + (0.000290
                * Math.sin(dr * 3 * M))
        var L = L0 + DL // true longitude, degree
        L -= 360 * INT(L / 360) // Normalize to (0, 360)
        return L
    }

    private fun NewMoon(k: Int): Double {
        // return CC2K.newMoonTime(k);
        return NewMoonAA98(k)
    }

    /**
     * Julian day number of the kth new moon after (or before) the New Moon of
     * 1900-01-01 13:51 GMT. Accuracy: 2 minutes Algorithm from: Astronomical
     * Algorithms, by Jean Meeus, 1998
     *
     * @param k
     * @return the Julian date number (number of days since noon UTC on 1
     * January 4713 BC) of the New Moon
     */
    private fun NewMoonAA98(k: Int): Double {
        val T = k / 1236.85 // Time in Julian centuries from 1900 January
        // 0.5
        val T2 = T * T
        val T3 = T2 * T
        val dr = PI / 180
        var Jd1 = (2415020.75933 + 29.53058868 * k + 0.0001178 * T2
                - 0.000000155 * T3)
        Jd1 += 0.00033 * Math.sin((166.56 + 132.87 * T - 0.009173 * T2) * dr) // Mean
        // new
        // moon
        val M = 359.2242 + 29.10535608 * k - 0.0000333 * T2 - (0.00000347
                * T3) // Sun's mean anomaly
        val Mpr = 306.0253 + 385.81691806 * k + 0.0107306 * T2 + (0.00001236
                * T3) // Moon's mean anomaly
        val F = 21.2964 + 390.67050646 * k - 0.0016528 * T2 - (0.00000239
                * T3) // Moon's argument of latitude
        var C1 = (0.1734 - 0.000393 * T) * sin(M * dr) + 0.0021 * sin(2 * dr * M)
        C1 = C1 - 0.4068 * Math.sin(Mpr * dr) + 0.0161 * Math.sin(dr * 2 * Mpr)
        C1 -= 0.0004 * sin(dr * 3 * Mpr)
        C1 = C1 + 0.0104 * sin(dr * 2 * F) - 0.0051 * sin(dr * (M + Mpr))
        C1 = C1 - 0.0074 * sin(dr * (M - Mpr)) + 0.0004 *sin(dr * (2 * F + M))
        C1 = C1 - 0.0004 * sin(dr * (2 * F - M)) - (0.0006
                * sin(dr * (2 * F + Mpr)))
        C1 += 0.0010 *
                sin(dr * (2 * F - Mpr)) + (0.0005
                * sin(dr * (2 * Mpr + M)))
        val deltat: Double = if (T < -11) {
            0.001 + 0.000839 * T + 0.0002261 * T2 - 0.00000845 * T3 - 0.000000081 * T * T3
        } else {
            -0.000278 + 0.000265 * T + 0.000262 * T2
        }
        return Jd1 + C1 - deltat
    }

    private fun INT(d: Double): Int {
        return floor(d).toInt()
    }

    private fun getSunLongitude(dayNumber: Int, timeZone: Double): Double {
        return SunLongitude(dayNumber - 0.5 - timeZone / 24)
    }

    private fun getNewMoonDay(k: Int, timeZone: Double): Int {
        val jd = NewMoon(k)
        return INT(jd + 0.5 + timeZone / 24)
    }

    private fun getLunarMonth11(yy: Int, timeZone: Double): Int {
        val off = jdFromDate(31, 12, yy) - 2415021.076998695
        val k = INT(off / 29.530588853)
        var nm = getNewMoonDay(k, timeZone)
        val sunLong = INT(getSunLongitude(nm, timeZone) / 30)
        if (sunLong >= 9) {
            nm = getNewMoonDay(k - 1, timeZone)
        }
        return nm
    }

    private fun getLeapMonthOffset(a11: Int, timeZone: Double): Int {
        val k = INT(0.5 + (a11 - 2415021.076998695) / 29.530588853)
        var last = 0 // Month 11 contains point of sun longutide 3*PI/2 (December
        // solstice)
        var i = 1 // We start with the month following lunar month 11
        var arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
        //		int arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) );
        do {
            last = arc
            i++
            arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone) / 30)
            //			arc = INT(getSunLongitude(getNewMoonDay(k + i, timeZone), timeZone));
        } while (arc != last && i < 14)
        return i - 1
    }

    /**
     *
     * @param solarDay
     * @param solarMonth
     * @param solarYear
     * @param timeZone
     * @return array of [lunarDay, lunarMonth, lunarYear, leapOrNot]
     */
    fun convertSolar2Lunar(
        solarDay: Int, solarMonth: Int, solarYear: Int,
        timeZone: Double
    ): IntArray {
        val lunarDay: Int
        var lunarMonth: Int
        var lunarYear: Int
        var lunarLeap: Int
        val dayNumber = jdFromDate(solarDay, solarMonth, solarYear)
        val k = INT((dayNumber - 2415021.076998695) / 29.530588853)
        var monthStart = getNewMoonDay(k + 1, timeZone)
        if (monthStart > dayNumber) {
            monthStart = getNewMoonDay(k, timeZone)
        }
        var a11 = getLunarMonth11(solarYear, timeZone)
        var b11 = a11
        if (a11 >= monthStart) {
            lunarYear = solarYear
            a11 = getLunarMonth11(solarYear - 1, timeZone)
        } else {
            lunarYear = solarYear + 1
            b11 = getLunarMonth11(solarYear + 1, timeZone)
        }
        lunarDay = dayNumber - monthStart + 1
        val diff = INT(((monthStart - a11) / 29).toDouble())
        lunarLeap = 0
        lunarMonth = diff + 11
        if (b11 - a11 > 365) {
            val leapMonthDiff = getLeapMonthOffset(a11, timeZone)
            //            Log.d(TAG,"xxxxxxxxxxxx- leapMonthDeff = "+leapMonthDiff);
            if (diff >= leapMonthDiff) {
                lunarMonth = diff + 10
                if (diff == leapMonthDiff) {
                    lunarLeap = 1
                }
            }
        }
        if (lunarMonth > 12) {
            lunarMonth = lunarMonth - 12
        }
        if (lunarMonth >= 11 && diff < 4) {
            lunarYear -= 1
        }
        return intArrayOf(lunarDay, lunarMonth, lunarYear, lunarLeap)
    }

    fun getWeek(year: Int, month: Int, day: Int) : String {
        val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("$year-$month-$day 00:00:00")
        calendarInstance.time = date
       return when(calendarInstance.get(7)) {
            1 -> "Chủ Nhật"
            2 -> "Thứ hai"
            3 -> "Thứ ba"
            4 -> "Thứ tư"
            5 -> "Thứ năm"
            6 -> "Thứ sáu"
            7 -> "Thứ bảy"
            else -> EMPTY
        }
    }

    fun getDay(): Int {
        return calendarInstance.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(): Int {
        return calendarInstance.get(Calendar.MONTH)
    }

    fun getYear(): Int {
        return calendarInstance.get(Calendar.YEAR)
    }

    private fun convertDay(solarDay: Int, solarMonth: Int, solarYear: Int): Int {
        val intValue = Integer.valueOf((14 - solarMonth) / 12).toInt()
        val i4 = (solarYear + 4800) - intValue
        val intValue2: Int =
            Integer.valueOf(((intValue * 12 + solarMonth - 3) * 153 + 2) / 5 + solarDay + i4 * 365 + i4 / 4)
                .toInt()
        val intValue3: Int =
            Integer.valueOf(intValue2 - i4 / 100 + i4 / 400 - 32045)
                .toInt()
        return if (intValue3 < 2299161) intValue2 - 32083 else intValue3
    }

    private fun handleSolarTerm(solarDay: Int, solarMonth: Int, solarYear: Int): Int {
        val doubleValue = (java.lang.Double.valueOf(convertDay(solarDay, solarMonth, solarYear).toDouble())
            .toDouble() - 2451545.0) / 36525.0
        val d = doubleValue * doubleValue
        val d2 = 35999.0503 * doubleValue + 357.5291 - 1.559E-4 * d - 4.8E-7 * doubleValue * d
        val sin =
            (0.019993 - 1.01E-4 * doubleValue) * sin(0.03490658503988659 * d2) + (1.9146 - 0.004817 * doubleValue - 1.4E-5 * d) * Math.sin(
                0.017453292519943295 * d2
            ) + sin(d2 * 0.05235987755982989) * 2.9E-4 + doubleValue * 36000.76983 + 280.46645 + d * 3.032E-4
        return (sin - java.lang.Double.valueOf((sin / 360.0).roundToInt().toDouble())
            .toDouble() * 360.0).roundToInt()
    }

    private fun join(collection: kotlin.collections.Collection<Any>, str: String): String {
        val sb = java.lang.StringBuilder()
        val it = collection.iterator()
        if (it.hasNext()) {
            sb.append(it.next().toString())
        }
        while (it.hasNext()) {
            sb.append(str)
            sb.append(it.next().toString())
        }
        return sb.toString()
    }

    fun getSolarTerm(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        val handleSolarTerm = handleSolarTerm(solarDay, solarMonth, solarYear)
        val numArr = arrSunLongitude
        val strArr = arrMinorSolarName
        var count = 0
        while (count < numArr.size) {
            if (handleSolarTerm == numArr[count]) {
                return strArr[count]
            }

            if (handleSolarTerm - numArr[count] >= 15) {
                ++count
            } else if (count == 23) {
                val str = strArr[count]
                val str2 = strArr[Constant.INDEX_DEFAULT]
                return "$str và $str2"
            } else {
                val str3 = strArr[count]
                val str4 = strArr[count + 1]
                return "$str3 và $str4"
            }

        }
        return EMPTY
    }

    fun getDepart(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        var str = ""
        var can = getCan(getDayCanChi(solarDay, solarMonth, solarYear)[0])
        if (can == Constant.INDEX_DEFAULT) {
            can = 12
        }
        var str2 = if (can == CONST_CAN_1 || can == CONST_CAN_6) "HỶ THẦN: Hướng Đông Bắc" else EMPTY
        if (can == CONST_CAN_2 || can == CONST_CAN_7) {
            str2 = "HỶ THẦN: Hướng Tây Bắc"
        }

        if (can == CONST_CAN_3 || can == CONST_CAN_8) {
            str2 = "HỶ THẦN: Hướng Tây Nam"
        }

        if (can == CONST_CAN_4 || can == CONST_CAN_9) {
            str2 = "HỶ THẦN: Hướng chính Nam";
        }
        if (can == CONST_CAN_5 || can == CONST_CAN_10) {
            str2 = "HỶ THẦN: Hướng Đông Nam";
        }
        if (can == CONST_CAN_1 || can == CONST_CAN_2) {
            str = "$str2 \nTÀI THẦN: Hướng Đông Nam";
        } else {
            str = str2;
        }
        if (can == CONST_CAN_3 || can == CONST_CAN_4) {
            str = "$str2 \nTÀI THẦN: Hướng chính Đông";
        }
        if (can == CONST_CAN_5) {
            str = "$str2 \nTÀI THẦN: Hướng Bắc";
        }
        if (can == CONST_CAN_6) {
            str = "$str2 \nTÀI THẦN: Hướng Nam";
        }
        if (can == CONST_CAN_7 || can == CONST_CAN_8) {
            str = "$str2 \nTÀI THẦN: Hướng Tây Nam";
        }
        if (can == CONST_CAN_9) {
            str = "$str2 \nTÀI THẦN: Hướng Tây";
        }
        if (can != CONST_CAN_10) {
            return str;
        }
        return "$str2 \nTÀI THẦN: Hướng Tây Bắc";

    }

    fun getStatusStar(solarYear: Int, lunarMonth: Int, lunarDay: Int, solarMonth: Int, solarDay: Int): String {
        val dayCanChi = getDayCanChi(solarDay, solarMonth, solarYear)
        val chi = getChi(dayCanChi[1]) + 1
        val can = getCan(dayCanChi[0]) + 1
        var str =
            if (solarDay === 20 && (solarMonth === 3 || solarMonth === 6) || solarDay === 22 && solarMonth === 9 || solarDay === 21 && solarMonth === 12) "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Ngày tứ ly : </font> <font style='font-family:roboto;font-size:18px'>Những ngày này khí vận suy kiệt, không nên dùng vào việc gì</font><br>" else ""
        if (solarDay === 3 && solarMonth === 2 || solarDay === 5 && solarMonth === 5 || solarDay === 8 && solarMonth === 8 || solarDay === 7 && solarMonth === 11) {
            str =
                "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Ngày tứ tuyệt : </font></font> <font style='font-family:roboto;font-size:18px'> Dùng việc gì cũng không lợi</font><br>"
        }
        if (lunarDay === 5 || lunarDay === 14 || lunarDay === 23) {
            str =
                "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Nguyệt kỵ : </font> </font></font> <font style='font-family:roboto;font-size:18px'> không nên khởi hành làm việc gì cả. </font><br>"
        }
        var str2 =
            if (lunarDay === 3 || lunarDay === 7 || lunarDay === 13 || lunarDay === 18 || lunarDay === 22 || lunarDay === 27) "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Tam nương : </font><font style='font-family:roboto;font-size:18px'> Trăm sự đều kỵ, chánh kỵ xuất hành</font><br>" else ""
        if (lunarDay === 13 && lunarMonth === 1 || lunarDay === 11 && lunarMonth === 2 || lunarDay === 9 && lunarMonth === 3 || lunarDay === 7 && lunarMonth === 4 || lunarDay === 5 && lunarMonth === 5 || lunarDay === 3 && lunarMonth === 6 || lunarDay === 8 && lunarMonth === 7 || lunarDay === 29 && lunarMonth === 7 || lunarDay === 27 && lunarMonth === 8 || lunarDay === 25 && lunarMonth === 9 || lunarDay === 23 && lunarMonth === 10 || lunarDay === 21 && lunarMonth === 11 || lunarDay === 19 && lunarMonth === 12) {
            str2 =
                "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Dương công kỵ nhật : </font><font style='font-family:roboto;font-size:18px'> ngày xấu nhất trong năm</font><br>"
        }
        if (lunarMonth === 1 && chi == 2 || lunarMonth === 2 && chi == 5 || lunarMonth === 3 && chi == 8 || lunarMonth === 4 && chi == 11 || lunarMonth === 5 && chi == 3 || lunarMonth === 6 && chi == 6 || lunarMonth === 7 && chi == 9 || lunarMonth === 8 && chi == 0 || lunarMonth === 9 && chi == 4 || lunarMonth === 10 && chi == 7 || lunarMonth === 11 && chi == 10 || lunarMonth === 12 && chi == 1) {
            str2 =
                "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Ngày vãng vong : </font><font style='font-family:roboto;font-size:18px'> Trăm sự đều kỵ, chánh kỵ xuất hành</font><br>"
        }
        if (lunarMonth === 1 && chi == 5 || lunarMonth === 2 && chi == 0 || lunarMonth === 3 && chi == 7 || lunarMonth === 4 && chi == 3 || lunarMonth === 5 && chi == 8 || lunarMonth === 6 && chi == 10 || lunarMonth === 7 && chi == 1 || lunarMonth === 8 && chi == 11 || lunarMonth === 9 && chi == 6 || lunarMonth === 10 && chi == 9 || lunarMonth === 11 && chi == 2 || lunarMonth === 12 && chi == 4) {
            str2 =
                "<font color = '#FF8E31' style='font-family:roboto;font-size:20px'>Ngày sát chủ : </font><font style='font-family:roboto;font-size:18px'> Kỵ xây cất, cưới gả</font><br>"
        }
        return "$str$str2<font color = '#006600' style='font-family:roboto;font-size:20px'>Sao tốt : </font>" + ("<font style='font-family:roboto;font-size:18px'>" + getGoodStar(
            lunarMonth,
            can,
            chi
        ) + "</font>") + "<br><br><font color = '#DA1500' style='font-family:roboto;font-size:20px'>Sao xấu : </font>" + ("<font style='font-family:roboto;font-size:18px'>" + getBadStar(
            lunarMonth,
            can,
            chi
        ) + "</font>")
    }


    private fun getBadStar(lunarMonth: Int, can: Int, chi: Int): String {
        val i4: Int = lunarMonth
        val i5: Int = can
        val i6: Int = chi
        val arrayList = ArrayList<Any>()
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_5 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_2 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Thiên Cương, Diệt Môn")
        }
        if (i4 === 1 && i6 === CONST_CHI_10 || i4 === 2 && i6 === CONST_CHI_7 || i4 === 3 && i6 === CONST_CHI_4 || i4 === 4 && i6 === CONST_CHI_1 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_7 || i4 === 7 && i6 === CONST_CHI_4 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_10 || i4 === 10 && i6 === CONST_CHI_7 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Thiên Lại ")
        }
        if (i4 === 1 && i6 === CONST_CHI_1 || i4 === 2 && i6 === CONST_CHI_4 || i4 === 3 && i6 === CONST_CHI_7 || i4 === 4 && i6 === CONST_CHI_10 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_4 || i4 === 7 && i6 === CONST_CHI_7 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_4 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_10) {
            arrayList.add("Thiên Ngục, Thiên Hoả ")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_6 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_2 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_2 || i4 === 10 && i6 === CONST_CHI_6 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Tiểu Hồng Sa ")
        }
        if (i4 === 1 && i6 === CONST_CHI_7 || i4 === 2 && i6 === CONST_CHI_8 || i4 === 3 && i6 === CONST_CHI_9 || i4 === 4 && i6 === CONST_CHI_10 || i4 === 5 && i6 === CONST_CHI_11 || i4 === 6 && i6 === CONST_CHI_12 || i4 === 7 && i6 === CONST_CHI_1 || i4 === 8 && i6 === CONST_CHI_2 || i4 === 9 && i6 === CONST_CHI_3 || i4 === 10 && i6 === CONST_CHI_4 || i4 === 11 && i6 === CONST_CHI_5 || i4 === 12 && i6 === CONST_CHI_6) {
            arrayList.add("Đại Hao (Tử khí, quan phú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_7 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_2 || i4 === 10 && i6 === CONST_CHI_3 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Tiểu Hao (Xấu về kinh doanh, cầu tài)")
        }
        if (i4 === 1 && i6 === CONST_CHI_9 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_11 || i4 === 4 && i6 === CONST_CHI_12 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_2 || i4 === 7 && i6 === CONST_CHI_3 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_5 || i4 === 10 && i6 === CONST_CHI_6 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_8) {
            arrayList.add("Nguyệt phá (Xấu về xây dựng nhà cửa)")
        }
        if (i4 === 1 && i6 === CONST_CHI_12 || i4 === 2 && i6 === CONST_CHI_9 || i4 === 3 && i6 === CONST_CHI_6 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_12 || i4 === 6 && i6 === CONST_CHI_9 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_3 || i4 === 9 && i6 === CONST_CHI_12 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_6 || i4 === 12 && i6 === CONST_CHI_3) {
            arrayList.add("Kiếp sát (Kỵ xuất hành, giá thú, an táng, xây dựng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_12 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_5 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Địa phá (Kỵ xây dựng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_3 || i4 === 2 && i6 === CONST_CHI_4 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_6 || i4 === 5 && i6 === CONST_CHI_7 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_9 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_1 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Thổ phủ (Kỵ xây dựng, động thổ)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_7 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_9 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_12 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_4) {
            arrayList.add("Thổ ôn, thiên cẩu (Kỵ xây dựng, đào ao, đào giếng, xấu về tế tự)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_7 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_9 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_12 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_4) {
            arrayList.add("Thổ ôn, thiên cẩu (Kỵ xây dựng, đào ao, đào giếng, xấu về tế tự)")
        }
        if (i4 === 1 && i6 === CONST_CHI_8 || i4 === 2 && i6 === CONST_CHI_11 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_7 || i4 === 6 && i6 === CONST_CHI_1 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_9 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_1 || i4 === 12 && i6 === CONST_CHI_4) {
            arrayList.add("Thiên ôn (Kỵ xây dựng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_11 || i4 === 2 && i6 === CONST_CHI_5 || i4 === 3 && i6 === CONST_CHI_12 || i4 === 4 && i6 === CONST_CHI_6 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_7 || i4 === 7 && i6 === CONST_CHI_2 || i4 === 8 && i6 === CONST_CHI_8 || i4 === 9 && i6 === CONST_CHI_3 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_10) {
            arrayList.add("Thụ tử (Xấu mọi việc trừ săn bắn)")
        }
        if (i4 === 1 && (i6 === CONST_CHI_6 || i6 === CONST_CHI_10 || i6 === CONST_CHI_2) || i4 === 2 && (i6 === CONST_CHI_6 || i6 === CONST_CHI_10 || i6 === CONST_CHI_2) || i4 === 3 && (i6 === CONST_CHI_6 || i6 === CONST_CHI_10 || i6 === CONST_CHI_2) || i4 === 4 && (i6 === CONST_CHI_9 || i6 === CONST_CHI_1 || i6 === CONST_CHI_5) || i4 === 5 && (i6 === CONST_CHI_9 || i6 === CONST_CHI_1 || i6 === CONST_CHI_5) || i4 === 6 && (i6 === CONST_CHI_9 || i6 === CONST_CHI_1 || i6 === CONST_CHI_5) || i4 === 7 && (i6 === CONST_CHI_12 || i6 === CONST_CHI_4 || i6 === CONST_CHI_8) || i4 === 8 && (i6 === CONST_CHI_12 || i6 === CONST_CHI_4 || i6 === CONST_CHI_8) || i4 === 9 && (i6 === CONST_CHI_12 || i6 === CONST_CHI_4 || i6 === CONST_CHI_8) || i4 === 10 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7 || i6 === CONST_CHI_11) || i4 === 11 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7 || i6 === CONST_CHI_11) || i4 === 12 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7 || i6 === CONST_CHI_11)) {
            arrayList.add("Hoang vu ")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_3 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_6 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_9 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_12) {
            arrayList.add("Thiên tặc (Xấu đối với khởi tạo, động thổ, nhập trạch, khai trương)")
        }
        if (i4 === 1 && i6 === CONST_CHI_2 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_12 || i4 === 4 && i6 === CONST_CHI_11 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_9 || i4 === 7 && i6 === CONST_CHI_8 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_5 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_3) {
            arrayList.add("Địa Tặc (Xấu đối với khởi tạo, an táng, động thổ, xuất hành)")
        }
        if (i4 === 1 && i6 === CONST_CHI_2 || i4 === 2 && i6 === CONST_CHI_8 || i4 === 3 && i6 === CONST_CHI_3 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_5 || i4 === 8 && i6 === CONST_CHI_11 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Hoả tai (Xấu đối với làm nhà, lợp nhà)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_5 || i4 === 3 && i6 === CONST_CHI_4 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_2 || i4 === 6 && i6 === CONST_CHI_1 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_11 || i4 === 9 && i6 === CONST_CHI_10 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_8 || i4 === 12 && i6 === CONST_CHI_7) {
            arrayList.add("Nguyệt Hoả, Độc Hoả (Xấu đối với lợp nhà, làm bếp)")
        }
        if (i4 === 1 && i6 === CONST_CHI_11 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_9 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_7 || i4 === 6 && i6 === CONST_CHI_6 || i4 === 7 && i6 === CONST_CHI_5 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_3 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_1 || i4 === 12 && i6 === CONST_CHI_12) {
            arrayList.add("Nguyệt Yếm đại hoạ (Xấu đối với xuất hành, giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_2 || i4 === 2 && i6 === CONST_CHI_11 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_5 || i4 === 5 && i6 === CONST_CHI_2 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_8 || i4 === 8 && i6 === CONST_CHI_5 || i4 === 9 && i6 === CONST_CHI_2 || i4 === 10 && i6 === CONST_CHI_11 || i4 === 11 && i6 === CONST_CHI_8 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Nguyệt Hư, Nguyệt Sát (Xấu đối với việc giá thú, mở cửa, mở hàng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_7 || i4 === 2 && i6 === CONST_CHI_3 || i4 === 3 && i6 === CONST_CHI_1 || i4 === 4 && i6 === CONST_CHI_7 || i4 === 5 && i6 === CONST_CHI_3 || i4 === 6 && i6 === CONST_CHI_1 || i4 === 7 && i6 === CONST_CHI_7 || i4 === 8 && i6 === CONST_CHI_3 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_7 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Hoàng Sa (Xấu đối với xuất hành)")
        }
        if (i4 === 1 && i6 === CONST_CHI_3 || i4 === 2 && i6 === CONST_CHI_7 || i4 === 3 && i6 === CONST_CHI_11 || i4 === 4 && i6 === CONST_CHI_6 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_2 || i4 === 7 && i6 === CONST_CHI_9 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_5 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_8) {
            arrayList.add("Lục Bất thành (Xấu đối với xây dựng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_10 || i4 === 2 && i6 === CONST_CHI_8 || i4 === 3 && i6 === CONST_CHI_6 || i4 === 4 && i6 === CONST_CHI_4 || i4 === 5 && i6 === CONST_CHI_2 || i4 === 6 && i6 === CONST_CHI_12 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_8 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_4 || i4 === 11 && i6 === CONST_CHI_2 || i4 === 12 && i6 === CONST_CHI_12) {
            arrayList.add("Nhân Cách (Xấu đối với giá thú, khởi tạo)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_4 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_12 || i4 === 5 && i6 === CONST_CHI_10 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_2 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_6 || i4 === 12 && i6 === CONST_CHI_8) {
            arrayList.add("Thần cách (Kỵ tế tự)")
        }
        if (i4 === 1 && i6 === CONST_CHI_1 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_7 || i4 === 4 && i6 === CONST_CHI_4 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_7 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_10 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_4) {
            arrayList.add("Phi Ma sát, Tai sát (Kỵ giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_7 || i4 === 2 && i6 === CONST_CHI_3 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_10 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_9 || i4 === 7 && i6 === CONST_CHI_2 || i4 === 8 && i6 === CONST_CHI_6 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_8 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Ngũ Quỹ (Kỵ xuất hành)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Băng tiêu ngoạ hãm ")
        }
        if (i4 === 1 && i6 === CONST_CHI_12 || i4 === 2 && i6 === CONST_CHI_7 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_3 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Hà khôi, Cẩu Giảo (Kỵ khởi công xây nhà cửa, xấu mọi việc)")
        }
        if (i4 === 1 && i6 === CONST_CHI_3 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_9 || i4 === 4 && i6 === CONST_CHI_12 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_7 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_5 || i4 === 10 && i6 === CONST_CHI_8 || i4 === 11 && i6 === CONST_CHI_11 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Vãng vong, Thổ kỵ (Kỵ xuất hành, giá thú, cầu tài lộc, động thổ)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_2 || i4 === 3 && i6 === CONST_CHI_11 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_1 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_3 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_9 || i4 === 12 && i6 === CONST_CHI_6) {
            arrayList.add("Cửu không (Kỵ xuất hành, cầu tài, khai trương)")
        }
        if (i4 === 1 && i5 === CONST_CAN_1 || i4 === 2 && i5 === CONST_CAN_2 || i4 === 3 && i5 === CONST_CAN_6 || i4 === 4 && i5 === CONST_CAN_3 || i4 === 5 && i5 === CONST_CAN_4 || i4 === 6 && i5 === CONST_CAN_6 || i4 === 7 && i5 === CONST_CAN_7 || i4 === 8 && i5 === CONST_CAN_8 || i4 === 9 && i5 === CONST_CAN_6 || i4 === 10 && i5 === CONST_CAN_9 || i4 === 11 && i5 === CONST_CAN_10 || i4 === 12 && i5 === CONST_CAN_6) {
            arrayList.add("Trùng Tang (Kỵ giá thú, an táng, khởi công xây nhà)")
        }
        if (i4 === 1 && i5 === CONST_CAN_7 || i4 === 2 && i5 === CONST_CAN_8 || i4 === 3 && i5 === CONST_CAN_6 || i4 === 4 && i5 === CONST_CAN_9 || i4 === 5 && i5 === CONST_CAN_10 || i4 === 6 && i5 === CONST_CAN_5 || i4 === 7 && i5 === CONST_CAN_1 || i4 === 8 && i5 === CONST_CAN_2 || i4 === 9 && i5 === CONST_CAN_6 || i4 === 10 && i5 === CONST_CAN_9 || i4 === 11 && i5 === CONST_CAN_10 || i4 === 12 && i5 === CONST_CAN_6) {
            arrayList.add("Trùng phục (Kỵ giá thú, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_4 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_10 || i4 === 5 && i6 === CONST_CHI_12 || i4 === 6 && i6 === CONST_CHI_2 || i4 === 7 && i6 === CONST_CHI_4 || i4 === 8 && i6 === CONST_CHI_6 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_10 || i4 === 11 && i6 === CONST_CHI_12 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Chu tước hắc đạo (Kỵ nhập trạch, khai trương)")
        }
        if (i4 === 1 && i6 === CONST_CHI_7 || i4 === 2 && i6 === CONST_CHI_9 || i4 === 3 && i6 === CONST_CHI_11 || i4 === 4 && i6 === CONST_CHI_1 || i4 === 5 && i6 === CONST_CHI_3 || i4 === 6 && i6 === CONST_CHI_5 || i4 === 7 && i6 === CONST_CHI_7 || i4 === 8 && i6 === CONST_CHI_9 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_1 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Bạch hổ (Kỵ mai táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_10 || i4 === 2 && i6 === CONST_CHI_12 || i4 === 3 && i6 === CONST_CHI_6 || i4 === 4 && i6 === CONST_CHI_4 || i4 === 5 && i6 === CONST_CHI_2 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_12 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_4 || i4 === 11 && i6 === CONST_CHI_2 || i4 === 12 && i6 === CONST_CHI_8) {
            arrayList.add("Huyền Vũ (Kỵ mai táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_12 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_4 || i4 === 4 && i6 === CONST_CHI_2 || i4 === 5 && i6 === CONST_CHI_8 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_1 || i4 === 9 && i6 === CONST_CHI_4 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_8 || i4 === 12 && i6 === CONST_CHI_10) {
            arrayList.add("Câu Trận (Kỵ mai táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_3 || i4 === 2 && i6 === CONST_CHI_12 || i4 === 3 && i6 === CONST_CHI_6 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_3 || i4 === 6 && i6 === CONST_CHI_12 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_9 || i4 === 9 && i6 === CONST_CHI_3 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_6 || i4 === 12 && i6 === CONST_CHI_9) {
            arrayList.add("Lôi công (Xấu với xây dựng nhà cửa)")
        }
        if (i4 === 1 && i6 === CONST_CHI_11 || i4 === 2 && i6 === CONST_CHI_12 || i4 === 3 && i6 === CONST_CHI_1 || i4 === 4 && i6 === CONST_CHI_2 || i4 === 5 && i6 === CONST_CHI_3 || i4 === 6 && i6 === CONST_CHI_4 || i4 === 7 && i6 === CONST_CHI_5 || i4 === 8 && i6 === CONST_CHI_6 || i4 === 9 && i6 === CONST_CHI_7 || i4 === 10 && i6 === CONST_CHI_8 || i4 === 11 && i6 === CONST_CHI_9 || i4 === 12 && i6 === CONST_CHI_10) {
            arrayList.add("Cô thần (Xấu với giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_7 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_9 || i4 === 6 && i6 === CONST_CHI_10 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_12 || i4 === 9 && i6 === CONST_CHI_1 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_4) {
            arrayList.add("Quả tú (Xấu với giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_4 || i4 === 5 && i6 === CONST_CHI_9 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_2 || i4 === 8 && i6 === CONST_CHI_12 || i4 === 9 && i6 === CONST_CHI_7 || i4 === 10 && i6 === CONST_CHI_10 || i4 === 11 && i6 === CONST_CHI_3 || i4 === 12 && i6 === CONST_CHI_5) {
            arrayList.add("Sát chủ ")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_7 || i4 === 6 && i6 === CONST_CHI_2 || i4 === 7 && i6 === CONST_CHI_3 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_12 || i4 === 11 && i6 === CONST_CHI_4 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Nguyệt Hình ")
        }
        if (i4 === 1 && i6 === CONST_CHI_7 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_8 || i4 === 4 && i6 === CONST_CHI_2 || i4 === 5 && i6 === CONST_CHI_9 || i4 === 6 && i6 === CONST_CHI_3 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_5 || i4 === 11 && i6 === CONST_CHI_12 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Tội chỉ (Xấu với tế tự, kiện cáo)")
        }
        if (i4 === 1 && i6 === CONST_CHI_4 || i4 === 2 && i6 === CONST_CHI_4 || i4 === 3 && i6 === CONST_CHI_4 || i4 === 4 && i6 === CONST_CHI_7 || i4 === 5 && i6 === CONST_CHI_7 || i4 === 6 && i6 === CONST_CHI_7 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_10 || i4 === 10 && i6 === CONST_CHI_1 || i4 === 11 && i6 === CONST_CHI_1 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Nguyệt Kiến chuyển sát (Kỵ động thổ)")
        }
        if (i4 === 1 && i6 === CONST_CHI_4 && i5 === CONST_CAN_10 || i4 === 2 && i6 === CONST_CHI_4 && i5 === CONST_CAN_10 || i4 === 3 && i6 === CONST_CHI_4 && i5 === CONST_CAN_10 || i4 === 4 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 5 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 6 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 7 && i6 === CONST_CHI_10 && i5 === CONST_CAN_4 || i4 === 8 && i6 === CONST_CHI_10 && i5 === CONST_CAN_4 || i4 === 9 && i6 === CONST_CHI_10 && i5 === CONST_CAN_4 || i4 === 10 && i6 === CONST_CHI_1 && i5 === CONST_CAN_7 || i4 === 11 && i6 === CONST_CHI_1 && i5 === CONST_CAN_7 || i4 === 12 && i6 === CONST_CHI_1 && i5 === CONST_CAN_7) {
            arrayList.add("Thiên địa chính chuyển (Kỵ động thổ)")
        }
        if (i4 === 1 && i6 === CONST_CHI_4 && i5 === CONST_CAN_2 || i4 === 2 && i6 === CONST_CHI_4 && i5 === CONST_CAN_2 || i4 === 3 && i6 === CONST_CHI_4 && i5 === CONST_CAN_2 || i4 === 4 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 5 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 6 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 7 && i6 === CONST_CHI_10 && i5 === CONST_CAN_8 || i4 === 8 && i6 === CONST_CHI_10 && i5 === CONST_CAN_8 || i4 === 9 && i6 === CONST_CHI_10 && i5 === CONST_CAN_8 || i4 === 10 && i6 === CONST_CHI_1 && i5 === CONST_CAN_9 || i4 === 11 && i6 === CONST_CHI_1 && i5 === CONST_CAN_9 || i4 === 12 && i6 === CONST_CHI_1 && i5 === CONST_CAN_9) {
            arrayList.add("Thiên địa chuyển sát (Kỵ động thổ)")
        }
        if (i4 === 1 && i6 === CONST_CHI_1 || i4 === 2 && i6 === CONST_CHI_1 || i4 === 3 && i6 === CONST_CHI_1 || i4 === 4 && i6 === CONST_CHI_4 || i4 === 5 && i6 === CONST_CHI_4 || i4 === 6 && i6 === CONST_CHI_4 || i4 === 7 && i6 === CONST_CHI_7 || i4 === 8 && i6 === CONST_CHI_7 || i4 === 9 && i6 === CONST_CHI_7 || i4 === 10 && i6 === CONST_CHI_10 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_10) {
            arrayList.add("Lỗ ban sát (Kỵ khởi tạo)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_5 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_8 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_10 || i4 === 8 && i6 === CONST_CHI_10 || i4 === 9 && i6 === CONST_CHI_10 || i4 === 10 && i6 === CONST_CHI_1 || i4 === 11 && i6 === CONST_CHI_1 || i4 === 12 && i6 === CONST_CHI_1) {
            arrayList.add("Phủ đầu sát (Kỵ khởi tạo)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_5 || i4 === 3 && i6 === CONST_CHI_5 || i4 === 4 && i6 === CONST_CHI_8 || i4 === 5 && i6 === CONST_CHI_8 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_11 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_2 || i4 === 11 && i6 === CONST_CHI_2 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Tam tang (Kỵ khởi tạo, giá thú, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_6 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_9 || i4 === 5 && i6 === CONST_CHI_1 || i4 === 6 && i6 === CONST_CHI_5 || i4 === 7 && i6 === CONST_CHI_12 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_3 || i4 === 11 && i6 === CONST_CHI_7 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Ngũ hư (Kỵ khởi tạo, giá thú, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_8 && i5 === CONST_CAN_2 || i4 === 2 && i6 === CONST_CHI_8 && i5 === CONST_CAN_2 || i4 === 3 && i6 === CONST_CHI_8 && i5 === CONST_CAN_2 || i4 === 4 && i6 === CONST_CHI_11 && i5 === CONST_CAN_3 || i4 === 5 && i6 === CONST_CHI_11 && i5 === CONST_CAN_3 || i4 === 6 && i6 === CONST_CHI_11 && i5 === CONST_CAN_3 || i4 === 7 && i6 === CONST_CHI_2 && i5 === CONST_CAN_8 || i4 === 8 && i6 === CONST_CHI_2 && i5 === CONST_CAN_8 || i4 === 9 && i6 === CONST_CHI_2 && i5 === CONST_CAN_8 || i4 === 10 && i6 === CONST_CHI_5 && i5 === CONST_CAN_9 || i4 === 11 && i6 === CONST_CHI_5 && i5 === CONST_CAN_9 || i4 === 12 && i6 === CONST_CHI_5 && i5 === CONST_CAN_9) {
            arrayList.add("Tứ thời đại mộ (Kỵ an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_12 || i4 === 2 && i6 === CONST_CHI_12 || i4 === 3 && i6 === CONST_CHI_12 || i4 === 4 && i6 === CONST_CHI_3 || i4 === 5 && i6 === CONST_CHI_3 || i4 === 6 && i6 === CONST_CHI_3 || i4 === 7 && i6 === CONST_CHI_6 || i4 === 8 && i6 === CONST_CHI_6 || i4 === 9 && i6 === CONST_CHI_6 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_9 || i4 === 12 && i6 === CONST_CHI_9) {
            arrayList.add("Thổ cẩm (Kỵ xây dựng, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_10 || i4 === 2 && i6 === CONST_CHI_10 || i4 === 3 && i6 === CONST_CHI_10 || i4 === 4 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7) || i4 === 5 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7) || i4 === 6 && (i6 === CONST_CHI_3 || i6 === CONST_CHI_7) || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_11 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_6 || i4 === 11 && i6 === CONST_CHI_6 || i4 === 12 && i6 === CONST_CHI_6) {
            arrayList.add("Ly sàng (Kỵ giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_2 || i4 === 2 && i6 === CONST_CHI_2 || i4 === 3 && i6 === CONST_CHI_2 || i4 === 4 && i6 === CONST_CHI_5 || i4 === 5 && i6 === CONST_CHI_5 || i4 === 6 && i6 === CONST_CHI_5 || i4 === 7 && i6 === CONST_CHI_8 || i4 === 8 && i6 === CONST_CHI_8 || i4 === 9 && i6 === CONST_CHI_8 || i4 === 10 && i6 === CONST_CHI_11 || i4 === 11 && i6 === CONST_CHI_11 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Tứ thời cô quả (Kỵ giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_5 || i4 === 2 && i6 === CONST_CHI_6 || i4 === 3 && i6 === CONST_CHI_1 || i4 === 4 && i6 === CONST_CHI_11 || i4 === 5 && i6 === CONST_CHI_12 || i4 === 6 && i6 === CONST_CHI_8 || i4 === 7 && i6 === CONST_CHI_3 || i4 === 8 && i6 === CONST_CHI_4 || i4 === 9 && i6 === CONST_CHI_7 || i4 === 10 && i6 === CONST_CHI_9 || i4 === 11 && i6 === CONST_CHI_10 || i4 === 12 && i6 === CONST_CHI_2) {
            arrayList.add("Không phòng (Kỵ giá thú)")
        }
        if (i4 === 1 && i6 === CONST_CHI_11 && i5 === CONST_CAN_7 || i4 === 2 && i6 === CONST_CHI_10 && i5 === CONST_CAN_8 || i4 === 3 && i6 === CONST_CHI_9 && i5 === CONST_CAN_7 || i4 === 4 && i6 === CONST_CHI_8 && i5 === CONST_CAN_4 || i4 === 5 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 6 && i6 === CONST_CHI_6 && i5 === CONST_CAN_4 || i4 === 7 && i6 === CONST_CHI_5 && i5 === CONST_CAN_1 || i4 === 8 && i6 === CONST_CHI_4 && i5 === CONST_CAN_2 || i4 === 9 && i6 === CONST_CHI_3 && i5 === CONST_CAN_1 || i4 === 10 && i6 === CONST_CHI_2 && i5 === CONST_CAN_10 || i4 === 11 && i6 === CONST_CHI_1 && i5 === CONST_CAN_9 || i4 === 12 && i6 === CONST_CHI_12 && i5 === CONST_CAN_10) {
            arrayList.add("Âm thác (Kỵ xuất hành, giá thú, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_3 && i5 === CONST_CAN_1 || i4 === 2 && i6 === CONST_CHI_4 && i5 === CONST_CAN_2 || i4 === 3 && i6 === CONST_CHI_5 && i5 === CONST_CAN_1 || i4 === 4 && i6 === CONST_CHI_6 && i5 === CONST_CAN_4 || i4 === 5 && i6 === CONST_CHI_7 && i5 === CONST_CAN_3 || i4 === 6 && i6 === CONST_CHI_8 && i5 === CONST_CAN_4 || i4 === 7 && i6 === CONST_CHI_9 && i5 === CONST_CAN_7 || i4 === 8 && i6 === CONST_CHI_10 && i5 === CONST_CAN_8 || i4 === 9 && i6 === CONST_CHI_11 && i5 === CONST_CAN_7 || i4 === 10 && i6 === CONST_CHI_12 && i5 === CONST_CAN_10 || i4 === 11 && i6 === CONST_CHI_1 && i5 === CONST_CAN_9 || i4 === 12 && i6 === CONST_CHI_2 && i5 === CONST_CAN_10) {
            arrayList.add("Dương thác (Kỵ xuất hành, giá thú, an táng)")
        }
        if (i4 === 1 && i6 === CONST_CHI_11 || i4 === 2 && i6 === CONST_CHI_11 || i4 === 3 && i6 === CONST_CHI_11 || i4 === 4 && i6 === CONST_CHI_11 || i4 === 5 && i6 === CONST_CHI_11 || i4 === 6 && i6 === CONST_CHI_11 || i4 === 7 && i6 === CONST_CHI_11 || i4 === 8 && i6 === CONST_CHI_11 || i4 === 9 && i6 === CONST_CHI_11 || i4 === 10 && i6 === CONST_CHI_11 || i4 === 11 && i6 === CONST_CHI_11 || i4 === 12 && i6 === CONST_CHI_11) {
            arrayList.add("Quỷ khốc (Xấu với tế tự, mai táng)")
        }
        return join(arrayList, ", ")
    }

    private fun getGoodStar(lunarMonth: Int, can: Int, chi: Int): String {
        val i4: Int = lunarMonth
        val i5: Int = can
        val i6: Int = chi
        val arrayList = ArrayList<Any>()
        if (i4 == 1 && i5 == CONST_CAN_4 || i4 == 2 && i6 == CONST_CHI_9 || i4 == 3 && i5 == CONST_CAN_9 || i4 == 4 && i5 == CONST_CAN_8 || i4 == 5 && i6 == CONST_CHI_12 || i4 == 6 && i5 == CONST_CAN_1 || i4 == 7 && i5 == CONST_CAN_10 || i4 == 8 && i6 == CONST_CHI_3 || i4 == 9 && i5 == CONST_CAN_3 || i4 == 10 && i5 == CONST_CAN_2 || i4 == 11 && i6 == CONST_CHI_6 || i4 == 12 && i6 == CONST_CAN_7) {
            arrayList.add("Thiên đức")
        }
        if (i4 == 1 && i5 == CONST_CAN_9 || i4 == 2 && i6 == CONST_CHI_1 || i4 == 3 && i5 == CONST_CAN_4 || i4 == 4 && i5 == CONST_CAN_3 || i4 == 5 && i6 == CONST_CHI_3 || i4 == 6 && i5 == CONST_CAN_6 || i4 == 7 && i5 == CONST_CAN_5 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i5 == CONST_CAN_8 || i4 == 10 && i5 == CONST_CAN_7 || i4 == 11 && i6 == CONST_CHI_9 || i4 == 12 && i6 == CONST_CAN_2) {
            arrayList.add("Thiên đức hợp")
        }
        if (i4 == 1 && i5 == CONST_CAN_3 || i4 == 2 && i5 == CONST_CAN_1 || i4 == 3 && i5 == CONST_CAN_9 || i4 == 4 && i5 == CONST_CAN_7 || i4 == 5 && i5 == CONST_CAN_3 || i4 == 6 && i5 == CONST_CAN_1 || i4 == 7 && i5 == CONST_CAN_9 || i4 == 8 && i5 == CONST_CAN_7 || i4 == 9 && i5 == CONST_CAN_3 || i4 == 10 && i5 == CONST_CAN_1 || i4 == 11 && i5 == CONST_CAN_9 || i4 == 12 && i5 == CONST_CAN_7) {
            arrayList.add("Nguyệt Đức")
        }
        if (i4 == 1 && i5 == CONST_CAN_8 || i4 == 2 && i5 == CONST_CAN_6 || i4 == 3 && i5 == CONST_CAN_4 || i4 == 4 && i5 == CONST_CAN_2 || i4 == 5 && i5 == CONST_CAN_8 || i4 == 6 && i5 == CONST_CAN_6 || i4 == 7 && i5 == CONST_CAN_4 || i4 == 8 && i5 == CONST_CAN_2 || i4 == 9 && i5 == CONST_CAN_8 || i4 == 10 && i5 == CONST_CAN_6 || i4 == 11 && i5 == CONST_CAN_4 || i4 == 12 && i5 == CONST_CAN_2) {
            arrayList.add("Nguyệt đức hợp(Tốt mọi việc, kỵ tố tụng)")
        }
        if (i4 == 1 && i6 == CONST_CHI_11 || i4 == 2 && i6 == CONST_CHI_12 || i4 == 3 && i6 == CONST_CHI_1 || i4 == 4 && i6 == CONST_CHI_2 || i4 == 5 && i6 == CONST_CHI_3 || i4 == 6 && i6 == CONST_CHI_4 || i4 == 7 && i6 == CONST_CHI_5 || i4 == 8 && i6 == CONST_CHI_6 || i4 == 9 && i6 == CONST_CHI_7 || i4 == 10 && i6 == CONST_CHI_8 || i4 == 11 && i6 == CONST_CHI_9 || i4 == 12 && i6 == CONST_CHI_10) {
            arrayList.add("Thiên hỷ(Tốt mọi việc, nhất là hôn thú)")
        }
        if (i4 == 1 && i6 == CONST_CHI_5 || i4 == 2 && i6 == CONST_CHI_6 || i4 == 3 && i6 == CONST_CHI_7 || i4 == 4 && i6 == CONST_CHI_8 || i4 == 5 && i6 == CONST_CHI_9 || i4 == 6 && i6 == CONST_CHI_10 || i4 == 7 && i6 == CONST_CHI_11 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_2 || i4 == 11 && i6 == CONST_CHI_3 || i4 == 12 && i6 == CONST_CHI_4) {
            arrayList.add("Thiên phú (Tốt mọi việc, nhất là xây dựng nhà cửa, khai trương và an táng)")
        }
        if (i4 == 1 && (i5 == CONST_CAN_1 || i5 == CONST_CAN_2) || i4 == 2 && (i5 == CONST_CAN_1 || i5 == CONST_CAN_2) || i4 == 3 && (i5 == CONST_CAN_1 || i5 == CONST_CAN_2) || i4 == 4 && (i5 == CONST_CAN_3 || i5 == CONST_CAN_4) || i4 == 5 && (i5 == CONST_CAN_3 || i5 == CONST_CAN_4) || i4 == 6 && (i5 == CONST_CAN_3 || i5 == CONST_CAN_4) || i4 == 7 && (i5 == CONST_CAN_7 || i5 == CONST_CAN_8) || i4 == 8 && (i5 == CONST_CAN_7 || i5 == CONST_CAN_8) || i4 == 9 && (i5 == CONST_CAN_7 || i5 == CONST_CAN_8) || i4 == 10 && (i5 == CONST_CAN_9 || i5 == CONST_CAN_10) || i4 == 11 && (i5 == CONST_CAN_9 || i5 == CONST_CAN_10) || i4 == 12 && (i5 == CONST_CAN_9 || i5 == CONST_CAN_10)) {
            arrayList.add("Thiên Quý ")
        }
        if (i4 == 1 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_3) || i4 == 2 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_3) || i4 == 3 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_3) || i4 == 4 && (i5 == CONST_CAN_1 || i6 == CONST_CHI_7) || i4 == 6 && (i5 == CONST_CAN_1 || i6 == CONST_CHI_7) || i4 == 7 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_9) || i4 == 8 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_9) || i4 == 9 && (i5 == CONST_CAN_5 || i6 == CONST_CHI_9) || i4 == 10 && (i5 == CONST_CAN_1 || i6 == CONST_CHI_1) || i4 == 12 && (i5 == CONST_CAN_1 || i6 == CONST_CHI_1)) {
            arrayList.add("Thiên Xá (Tốt cho tế tự, giải oan, trừ được các sao xấu, chỉ kiêng kỵ động thổ. Nếu gặp trực khai thì rất tốt tức là ngày thiên xá gặp sinh khí)")
        }
        if (i4 == 1 && i6 == CONST_CHI_1 || i4 == 2 && i6 == CONST_CHI_2 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_4 || i4 == 5 && i6 == CONST_CHI_5 || i4 == 6 && i6 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_7 || i4 == 8 && i6 == CONST_CHI_8 || i4 == 9 && i6 == CONST_CHI_9 || i4 == 10 && i6 == CONST_CHI_10 || i4 == 11 && i6 == CONST_CHI_11 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Sinh khí (Tốt mọi việc, nhất là làm nhà, sửa nhà, động thổ, trồng cây)")
        }
        if (i4 == 1 && i5 == CONST_CAN_6 || i4 == 2 && i5 == CONST_CAN_5 || i4 == 4 && (i5 == CONST_CAN_8 || i5 == CONST_CAN_10) || i4 == 5 && (i5 == CONST_CAN_8 || i5 == CONST_CAN_9) || i4 == 7 && i5 == CONST_CAN_2 || i4 == 8 && i5 == CONST_CAN_1 || i4 == 10 && i5 == CONST_CAN_4 || i4 == 11 && i5 == CONST_CAN_3) {
            arrayList.add("Thiên Phúc ")
        }
        if (i4 == 1 && i6 == CONST_CHI_8 || i4 == 2 && i6 == CONST_CHI_10 || i4 == 3 && i6 == CONST_CHI_12 || i4 == 4 && i6 == CONST_CHI_2 || i4 == 5 && i6 == CONST_CHI_4 || i4 == 6 && i6 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_8 || i4 == 8 && i6 == CONST_CHI_10 || i4 == 9 && i6 == CONST_CHI_12 || i4 == 10 && i6 == CONST_CHI_2 || i4 == 11 && i6 == CONST_CHI_4 || i4 == 12 && i6 == CONST_CHI_6) {
            arrayList.add("Thiên thành ")
        }
        if (i4 == 1 && i6 == CONST_CHI_11 || i4 == 2 && i6 == CONST_CHI_1 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_5 || i4 == 5 && i6 == CONST_CHI_7 || i4 == 6 && i6 == CONST_CHI_9 || i4 == 7 && i6 == CONST_CHI_11 || i4 == 8 && i6 == CONST_CHI_1 || i4 == 9 && i6 == CONST_CHI_3 || i4 == 10 && i6 == CONST_CHI_5 || i4 == 11 && i6 == CONST_CHI_7 || i4 == 12 && i6 == CONST_CHI_9) {
            arrayList.add("Thiên Quan ")
        }
        if (i4 == 1 && i6 == CONST_CHI_7 || i4 == 2 && i6 == CONST_CHI_9 || i4 == 3 && i6 == CONST_CHI_11 || i4 == 4 && i6 == CONST_CHI_1 || i4 == 5 && i6 == CONST_CHI_3 || i4 == 6 && i6 == CONST_CHI_5 || i4 == 7 && i6 == CONST_CHI_7 || i4 == 8 && i6 == CONST_CHI_9 || i4 == 9 && i6 == CONST_CHI_11 || i4 == 10 && i6 == CONST_CHI_1 || i4 == 11 && i6 == CONST_CHI_3 || i4 == 12 && i6 == CONST_CHI_5) {
            arrayList.add("Thiên Mã (Tốt cho việc xuất hành, giao dịch, cầu tài lộc)")
        }
        if (i4 == 1 && i6 == CONST_CHI_5 || i4 == 2 && i6 == CONST_CHI_7 || i4 == 3 && i6 == CONST_CHI_9 || i4 == 4 && i6 == CONST_CHI_11 || i4 == 5 && i6 == CONST_CHI_1 || i4 == 6 && i6 == CONST_CHI_3 || i4 == 7 && i6 == CONST_CHI_5 || i4 == 8 && i6 == CONST_CHI_7 || i4 == 9 && i6 == CONST_CHI_9 || i4 == 10 && i6 == CONST_CHI_11 || i4 == 11 && i6 == CONST_CHI_1 || i4 == 12 && i6 == CONST_CHI_3) {
            arrayList.add("Thiên tài (Tốt cho việc cầu tài lộc, khai trương)")
        }
        if (i4 == 1 && i6 == CONST_CHI_6 || i4 == 2 && i6 == CONST_CHI_8 || i4 == 3 && i6 == CONST_CHI_10 || i4 == 4 && i6 == CONST_CHI_12 || i4 == 5 && i6 == CONST_CHI_2 || i4 == 6 && i6 == CONST_CHI_4 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_8 || i4 == 9 && i6 == CONST_CHI_10 || i4 == 10 && i6 == CONST_CHI_12 || i4 == 11 && i6 == CONST_CHI_2 || i4 == 12 && i6 == CONST_CHI_4) {
            arrayList.add("Địa tài (Tốt cho việc cầu tài lộc, khai trương)")
        }
        if (i4 == 1 && i6 == CONST_CHI_7 || i4 == 2 && i6 == CONST_CHI_6 || i4 == 3 && i6 == CONST_CHI_6 || i4 == 4 && i6 == CONST_CHI_8 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i6 == CONST_CHI_12 || i4 == 7 && i6 == CONST_CHI_7 || i4 == 8 && i6 == CONST_CHI_6 || i4 == 9 && i6 == CONST_CHI_6 || i4 == 10 && i6 == CONST_CHI_8 || i4 == 11 && i6 == CONST_CHI_10 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Nguyệt Tài (Tốt cho việc cầu tài lộc, khai trương, xuất hành, di chuyển, giao dịch)")
        }
        if (i4 == 1 && i5 == CONST_CAN_3 || i4 == 2 && i5 == CONST_CAN_4 || i4 == 3 && i5 == CONST_CAN_7 || i4 == 4 && i5 == CONST_CAN_6 || i4 == 5 && i5 == CONST_CAN_5 || i4 == 6 && i5 == CONST_CAN_8 || i4 == 7 && i5 == CONST_CAN_9 || i4 == 8 && i5 == CONST_CAN_10 || i4 == 9 && i5 == CONST_CAN_7 || i4 == 10 && i5 == CONST_CAN_2 || i4 == 11 && i5 == CONST_CAN_1 || i4 == 12 && i5 == CONST_CAN_8) {
            arrayList.add("Nguyệt Ân ")
        }
        if (i4 == 1 && i5 == CONST_CAN_9 || i4 == 2 && i5 == CONST_CAN_7 || i4 == 3 && i5 == CONST_CAN_3 || i4 == 4 && i5 == CONST_CAN_1 || i4 == 5 && i5 == CONST_CAN_9 || i4 == 6 && i5 == CONST_CAN_7 || i4 == 7 && i5 == CONST_CAN_3 || i4 == 8 && i5 == CONST_CAN_1 || i4 == 9 && i5 == CONST_CAN_9 || i4 == 10 && i5 == CONST_CAN_7 || i4 == 11 && i5 == CONST_CAN_3 || i4 == 12 && i5 == CONST_CAN_1) {
            arrayList.add("Nguyệt Không (Tốt cho việc làm nhà, làm gường)")
        }
        if (i4 == 1 && i6 == CONST_CHI_9 || i4 == 2 && i6 == CONST_CHI_11 || i4 == 3 && i6 == CONST_CHI_1 || i4 == 4 && i6 == CONST_CHI_3 || i4 == 5 && i6 == CONST_CHI_5 || i4 == 6 && i5 == CONST_CHI_7 || i4 == 7 && i6 == CONST_CHI_9 || i4 == 8 && i6 == CONST_CHI_11 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_3 || i4 == 11 && i6 == CONST_CHI_5 || i4 == 12 && i6 == CONST_CHI_7) {
            arrayList.add("Minh tinh ")
        }
        if (i4 == 1 && i6 == CONST_CHI_12 || i4 == 2 && i6 == CONST_CHI_6 || i4 == 3 && i6 == CONST_CHI_1 || i4 == 4 && i6 == CONST_CHI_7 || i4 == 5 && i6 == CONST_CHI_2 || i4 == 6 && i5 == CONST_CHI_8 || i4 == 7 && i6 == CONST_CHI_3 || i4 == 8 && i6 == CONST_CHI_9 || i4 == 9 && i6 == CONST_CHI_4 || i4 == 10 && i6 == CONST_CHI_10 || i4 == 11 && i6 == CONST_CHI_5 || i4 == 12 && i6 == CONST_CHI_11) {
            arrayList.add("Thánh tâm (Tốt mọi việc, nhất là cầu phúc, tế tự)")
        }
        if (i4 == 1 && i6 == CONST_CHI_12 || i4 == 2 && i6 == CONST_CHI_3 || i4 == 3 && i6 == CONST_CHI_6 || i4 == 4 && i6 == CONST_CHI_9 || i4 == 5 && i6 == CONST_CHI_12 || i4 == 6 && i5 == CONST_CHI_3 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_9 || i4 == 9 && i6 == CONST_CHI_12 || i4 == 10 && i6 == CONST_CHI_3 || i4 == 11 && i6 == CONST_CHI_6 || i4 == 12 && i6 == CONST_CHI_9) {
            arrayList.add("Ngũ phú ")
        }
        if (i4 == 1 && i6 == CONST_CHI_5 || i4 == 2 && i6 == CONST_CHI_6 || i4 == 3 && i6 == CONST_CHI_7 || i4 == 4 && i6 == CONST_CHI_8 || i4 == 5 && i6 == CONST_CHI_9 || i4 == 6 && i5 == CONST_CHI_10 || i4 == 7 && i6 == CONST_CHI_11 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_2 || i4 == 11 && i6 == CONST_CHI_3 || i4 == 12 && i6 == CONST_CHI_4) {
            arrayList.add("Lộc khố (Tốt cho việc cầu tài, khai trương, giao dịch)")
        }
        if (i4 == 1 && i6 == CONST_CHI_10 || i4 == 2 && i6 == CONST_CHI_4 || i4 == 3 && i6 == CONST_CHI_11 || i4 == 4 && i6 == CONST_CHI_5 || i4 == 5 && i6 == CONST_CHI_12 || i4 == 6 && i5 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_1 || i4 == 8 && i6 == CONST_CHI_7 || i4 == 9 && i6 == CONST_CHI_2 || i4 == 10 && i6 == CONST_CHI_8 || i4 == 11 && i6 == CONST_CHI_3 || i4 == 12 && i6 == CONST_CHI_9) {
            arrayList.add("Phúc Sinh ")
        }
        if (i4 == 1 && i6 == CONST_CHI_10 || i4 == 2 && i6 == CONST_CHI_3 || i4 == 3 && i6 == CONST_CHI_12 || i4 == 4 && i6 == CONST_CHI_5 || i4 == 5 && i6 == CONST_CHI_2 || i4 == 6 && i5 == CONST_CHI_7 || i4 == 7 && i6 == CONST_CHI_4 || i4 == 8 && i6 == CONST_CHI_9 || i4 == 9 && i6 == CONST_CHI_6 || i4 == 10 && i6 == CONST_CHI_11 || i4 == 11 && i6 == CONST_CHI_8 || i4 == 12 && i6 == CONST_CHI_1) {
            arrayList.add("Cát Khánh ")
        }
        if (i4 == 1 && i6 == CONST_CHI_10 || i4 == 2 && i6 == CONST_CHI_8 || i4 == 3 && i6 == CONST_CHI_6 || i4 == 4 && i6 == CONST_CHI_4 || i4 == 5 && i6 == CONST_CHI_2 || i4 == 6 && i5 == CONST_CHI_12 || i4 == 7 && i6 == CONST_CHI_10 || i4 == 8 && i6 == CONST_CHI_8 || i4 == 9 && i6 == CONST_CHI_6 || i4 == 10 && i6 == CONST_CHI_4 || i4 == 11 && i6 == CONST_CHI_2 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Âm Đức ")
        }
        if (i4 == 1 && i6 == CONST_CHI_12 || i4 == 2 && i6 == CONST_CHI_5 || i4 == 3 && i6 == CONST_CHI_2 || i4 == 4 && i6 == CONST_CHI_7 || i4 == 5 && i6 == CONST_CHI_4 || i4 == 6 && i5 == CONST_CHI_9 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_11 || i4 == 9 && i6 == CONST_CHI_8 || i4 == 10 && i6 == CONST_CHI_6 || i4 == 11 && i6 == CONST_CHI_10 || i4 == 12 && i6 == CONST_CHI_3) {
            arrayList.add("U Vi tinh ")
        }
        if (i4 == 1 && i6 == CONST_CHI_3 || i4 == 2 && i6 == CONST_CHI_8 || i4 == 3 && i6 == CONST_CHI_5 || i4 == 4 && i6 == CONST_CHI_10 || i4 == 5 && i6 == CONST_CHI_7 || i4 == 6 && i5 == CONST_CHI_12 || i4 == 7 && i6 == CONST_CHI_9 || i4 == 8 && i6 == CONST_CHI_2 || i4 == 9 && i6 == CONST_CHI_11 || i4 == 10 && i6 == CONST_CHI_4 || i4 == 11 && i6 == CONST_CHI_1 || i4 == 12 && i6 == CONST_CHI_6) {
            arrayList.add("Mãn đức tinh ")
        }
        if (i4 == 1 && i6 == CONST_CHI_8 || i4 == 2 && i6 == CONST_CHI_2 || i4 == 3 && i6 == CONST_CHI_9 || i4 == 4 && i6 == CONST_CHI_3 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i5 == CONST_CHI_4 || i4 == 7 && i6 == CONST_CHI_11 || i4 == 8 && i6 == CONST_CHI_5 || i4 == 9 && i6 == CONST_CHI_12 || i4 == 10 && i6 == CONST_CHI_6 || i4 == 11 && i6 == CONST_CHI_1 || i4 == 12 && i6 == CONST_CHI_7) {
            arrayList.add("Kính Tâm (Tốt đối với tang lễ)")
        }
        if (i4 == 1 && i6 == CONST_CHI_2 || i4 == 2 && i6 == CONST_CHI_1 || i4 == 3 && i6 == CONST_CHI_12 || i4 == 4 && i6 == CONST_CHI_11 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i5 == CONST_CHI_9 || i4 == 7 && i6 == CONST_CHI_8 || i4 == 8 && i6 == CONST_CHI_7 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_5 || i4 == 11 && i6 == CONST_CHI_4 || i4 == 12 && i6 == CONST_CHI_3) {
            arrayList.add("Tuế hợp ")
        }
        if (i4 == 1 && i6 == CONST_CHI_9 || i4 == 2 && i6 == CONST_CHI_9 || i4 == 3 && i6 == CONST_CHI_10 || i4 == 4 && i6 == CONST_CHI_10 || i4 == 5 && i6 == CONST_CHI_11 || i4 == 6 && i5 == CONST_CHI_11 || i4 == 7 && i6 == CONST_CHI_12 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i6 == CONST_CHI_7 || i4 == 10 && i6 == CONST_CHI_7 || i4 == 11 && i6 == CONST_CHI_8 || i4 == 12 && i6 == CONST_CHI_8) {
            arrayList.add("Nguyệt giải ")
        }
        if (i4 == 2 && i6 == CONST_CHI_4 || i4 == 5 && i6 == CONST_CHI_7 || i4 == 8 && i6 == CONST_CHI_10 || i4 == 11 && i6 == CONST_CHI_1) {
            arrayList.add("Quan nhật ")
        }
        if (i4 == 1 && i6 == CONST_CHI_6 || i4 == 2 && i6 == CONST_CHI_11 || i4 == 3 && i6 == CONST_CHI_8 || i4 == 4 && i6 == CONST_CHI_1 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i5 == CONST_CHI_3 || i4 == 7 && i6 == CONST_CHI_12 || i4 == 8 && i6 == CONST_CHI_5 || i4 == 9 && i6 == CONST_CHI_2 || i4 == 10 && i6 == CONST_CHI_7 || i4 == 11 && i6 == CONST_CHI_4 || i4 == 12 && i6 == CONST_CHI_9) {
            arrayList.add("Hoạt điệu (Tốt, nhưng gặp thụ tử thì xấu)")
        }
        if (i4 == 1 && i6 == CONST_CHI_9 || i4 == 2 && i6 == CONST_CHI_9 || i4 == 3 && i6 == CONST_CHI_11 || i4 == 4 && i6 == CONST_CHI_11 || i4 == 5 && i6 == CONST_CHI_1 || i4 == 6 && i5 == CONST_CHI_1 || i4 == 7 && i6 == CONST_CHI_3 || i4 == 8 && i6 == CONST_CHI_3 || i4 == 9 && i6 == CONST_CHI_5 || i4 == 10 && i6 == CONST_CHI_5 || i4 == 11 && i6 == CONST_CHI_7 || i4 == 12 && i6 == CONST_CHI_7) {
            arrayList.add("Giải thần (Tốt cho việc tế tự, tố tụng, giải oan trừ được các sao xấu)")
        }
        if (i4 == 1 && i6 == CONST_CHI_9 || i4 == 2 && i6 == CONST_CHI_3 || i4 == 3 && i6 == CONST_CHI_10 || i4 == 4 && i6 == CONST_CHI_4 || i4 == 5 && i6 == CONST_CHI_11 || i4 == 6 && i5 == CONST_CHI_5 || i4 == 7 && i6 == CONST_CHI_12 || i4 == 8 && i6 == CONST_CHI_6 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_7 || i4 == 11 && i6 == CONST_CHI_2 || i4 == 12 && i6 == CONST_CHI_8) {
            arrayList.add("Phổ hộ (Tốt mọi việc, làm phúc, giá thú, xuất hành)")
        }
        if (i4 == 1 && i6 == CONST_CHI_1 || i4 == 2 && i6 == CONST_CHI_7 || i4 == 3 && i6 == CONST_CHI_2 || i4 == 4 && i6 == CONST_CHI_8 || i4 == 5 && i6 == CONST_CHI_3 || i4 == 6 && i5 == CONST_CHI_9 || i4 == 7 && i6 == CONST_CHI_4 || i4 == 8 && i6 == CONST_CHI_10 || i4 == 9 && i6 == CONST_CHI_5 || i4 == 10 && i6 == CONST_CHI_11 || i4 == 11 && i6 == CONST_CHI_6 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Ích Hậu (Tốt mọi việc, nhất là giá thú)")
        }
        if (i4 == 1 && i6 == CONST_CHI_2 || i4 == 2 && i6 == CONST_CHI_8 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_9 || i4 == 5 && i6 == CONST_CHI_4 || i4 == 6 && i5 == CONST_CHI_10 || i4 == 7 && i6 == CONST_CHI_5 || i4 == 8 && i6 == CONST_CHI_11 || i4 == 9 && i6 == CONST_CHI_6 || i4 == 10 && i6 == CONST_CHI_12 || i4 == 11 && i6 == CONST_CHI_7 || i4 == 12 && i6 == CONST_CHI_1) {
            arrayList.add("Tục Thế (Tốt mọi việc, nhất là giá thú)")
        }
        if (i4 == 1 && i6 == CONST_CHI_3 || i4 == 2 && i6 == CONST_CHI_9 || i4 == 3 && i6 == CONST_CHI_4 || i4 == 4 && i6 == CONST_CHI_10 || i4 == 5 && i6 == CONST_CHI_5 || i4 == 6 && i5 == CONST_CHI_11 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i6 == CONST_CHI_7 || i4 == 10 && i6 == CONST_CHI_1 || i4 == 11 && i6 == CONST_CHI_8 || i4 == 12 && i6 == CONST_CHI_2) {
            arrayList.add("Yếu yên (Tốt mọi việc, nhất là giá thú)")
        }
        if (i4 == 1 && i6 == CONST_CHI_9 || i4 == 2 && i6 == CONST_CHI_6 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_12 || i4 == 5 && i6 == CONST_CHI_9 || i4 == 6 && i5 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_3 || i4 == 8 && i6 == CONST_CHI_12 || i4 == 9 && i6 == CONST_CHI_9 || i4 == 10 && i6 == CONST_CHI_6 || i4 == 11 && i6 == CONST_CHI_3 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Dịch Mã (Tốt mọi việc, nhất là xuất hành)")
        }
        if (i4 == 1 && (i6 == CONST_CHI_7 || i6 == CONST_CHI_11) || i4 == 2 && (i6 == CONST_CHI_8 || i6 == CONST_CHI_12) || i4 == 3 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_1) || i4 == 4 && (i6 == CONST_CHI_10 || i6 == CONST_CHI_2) || i4 == 5 && (i6 == CONST_CHI_11 || i6 == CONST_CHI_3) || i4 == 6 && (i6 == CONST_CHI_12 || i6 == CONST_CHI_4) || i4 == 7 && (i6 == CONST_CHI_1 || i6 == CONST_CHI_5) || i4 == 8 && (i6 == CONST_CHI_2 || i6 == CONST_CHI_6) || i4 == 9 && (i6 == CONST_CHI_3 || i6 == CONST_CHI_7) || i4 == 10 && (i6 == CONST_CHI_4 || i6 == CONST_CHI_10) || i4 == 11 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_9) || i4 == 12 && (i6 == CONST_CHI_6 || i6 == CONST_CHI_10)) {
            arrayList.add("Tam Hợp ")
        }
        if (i4 == 1 && i6 == CONST_CHI_12 || i4 == 2 && i6 == CONST_CHI_11 || i4 == 3 && i6 == CONST_CHI_10 || i4 == 4 && i6 == CONST_CHI_9 || i4 == 5 && i6 == CONST_CHI_8 || i4 == 6 && i5 == CONST_CHI_7 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_5 || i4 == 9 && i6 == CONST_CHI_4 || i4 == 10 && i6 == CONST_CHI_3 || i4 == 11 && i6 == CONST_CHI_2 || i4 == 12 && i6 == CONST_CHI_1) {
            arrayList.add("Lục Hợp ")
        }
        if (i4 == 1 && (i6 == CONST_CHI_12 || i6 == CONST_CHI_1) || i4 == 2 && (i6 == CONST_CHI_12 || i6 == CONST_CHI_1) || i4 == 3 && (i6 == CONST_CHI_12 || i6 == CONST_CHI_1) || i4 == 4 && (i6 == CONST_CHI_3 || i6 == CONST_CHI_4) || i4 == 5 && (i6 == CONST_CHI_3 || i6 == CONST_CHI_4) || i4 == 6 && (i6 == CONST_CHI_3 || i6 == CONST_CHI_4) || i4 == 7 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_2) || i4 == 8 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_2) || i4 == 9 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_2) || i4 == 10 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_10) || i4 == 11 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_10) || i4 == 12 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_10)) {
            arrayList.add("Mẫu Thương (Tốt về cầu tài lộc, khai trương)")
        }
        if (i4 == 1 && i6 == CONST_CHI_3 || i4 == 2 && i6 == CONST_CHI_3 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_6 || i4 == 5 && i6 == CONST_CHI_6 || i4 == 6 && i6 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_9 || i4 == 8 && i6 == CONST_CHI_9 || i4 == 9 && i6 == CONST_CHI_9 || i4 == 10 && i6 == CONST_CHI_12 || i4 == 11 && i6 == CONST_CHI_12 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Phúc hậu (Tốt về cầu tài lộc, khai trương)")
        }
        if (i4 == 1 && (i6 == CONST_CHI_1 || i6 == CONST_CHI_2) || i4 == 2 && (i6 == CONST_CHI_1 || i6 == CONST_CHI_2) || i4 == 3 && (i6 == CONST_CHI_1 || i6 == CONST_CHI_2) || i4 == 4 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_6) || i4 == 5 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_6) || i4 == 6 && (i6 == CONST_CHI_5 || i6 == CONST_CHI_6) || i4 == 7 && (i6 == CONST_CHI_7 || i6 == CONST_CHI_8) || i4 == 8 && (i6 == CONST_CHI_7 || i6 == CONST_CHI_8) || i4 == 9 && (i6 == CONST_CHI_7 || i6 == CONST_CHI_8) || i4 == 10 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_11) || i4 == 11 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_11) || i4 == 12 && (i6 == CONST_CHI_9 || i6 == CONST_CHI_11)) {
            arrayList.add("Đại Hồng Sa ")
        }
        if (i4 == 1 && i6 == CONST_CHI_7 || i4 == 2 && i6 == CONST_CHI_7 || i4 == 3 && i6 == CONST_CHI_7 || i4 == 4 && i6 == CONST_CHI_10 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i6 == CONST_CHI_10 || i4 == 7 && i6 == CONST_CHI_1 || i4 == 8 && i6 == CONST_CHI_1 || i4 == 9 && i6 == CONST_CHI_1 || i4 == 10 && i6 == CONST_CHI_4 || i4 == 11 && i6 == CONST_CHI_4 || i4 == 12 && i6 == CONST_CHI_4) {
            arrayList.add("Dân nhật, thời đức ")
        }
        if (i4 == 1 && i6 == CONST_CHI_11 || i4 == 2 && i6 == CONST_CHI_2 || i4 == 3 && i6 == CONST_CHI_3 || i4 == 4 && i6 == CONST_CHI_6 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i6 == CONST_CHI_4 || i4 == 7 && i6 == CONST_CHI_1 || i4 == 8 && i6 == CONST_CHI_7 || i4 == 9 && i6 == CONST_CHI_12 || i4 == 10 && i6 == CONST_CHI_5 || i4 == 11 && i6 == CONST_CHI_9 || i4 == 12 && i6 == CONST_CHI_8) {
            arrayList.add("Hoàng Ân")
        }
        if (i4 == 1 && i6 == CONST_CHI_1 || i4 == 2 && i6 == CONST_CHI_3 || i4 == 3 && i6 == CONST_CHI_5 || i4 == 4 && i6 == CONST_CHI_7 || i4 == 5 && i6 == CONST_CHI_9 || i4 == 6 && i6 == CONST_CHI_11 || i4 == 7 && i6 == CONST_CHI_1 || i4 == 8 && i6 == CONST_CHI_3 || i4 == 9 && i6 == CONST_CHI_5 || i4 == 10 && i6 == CONST_CHI_7 || i4 == 11 && i6 == CONST_CHI_9 || i4 == 12 && i6 == CONST_CHI_11) {
            arrayList.add("Thanh Long ")
        }
        if (i4 == 1 && i6 == CONST_CHI_2 || i4 == 2 && i6 == CONST_CHI_4 || i4 == 3 && i6 == CONST_CHI_6 || i4 == 4 && i6 == CONST_CHI_8 || i4 == 5 && i6 == CONST_CHI_10 || i4 == 6 && i6 == CONST_CHI_12 || i4 == 7 && i6 == CONST_CHI_2 || i4 == 8 && i6 == CONST_CHI_4 || i4 == 9 && i6 == CONST_CHI_6 || i4 == 10 && i6 == CONST_CHI_8 || i4 == 11 && i6 == CONST_CHI_10 || i4 == 12 && i6 == CONST_CHI_12) {
            arrayList.add("Minh đường ")
        }
        if (i4 == 1 && i6 == CONST_CHI_6 || i4 == 2 && i6 == CONST_CHI_8 || i4 == 3 && i6 == CONST_CHI_10 || i4 == 4 && i6 == CONST_CHI_12 || i4 == 5 && i6 == CONST_CHI_2 || i4 == 6 && i6 == CONST_CHI_4 || i4 == 7 && i6 == CONST_CHI_6 || i4 == 8 && i6 == CONST_CHI_8 || i4 == 9 && i6 == CONST_CHI_10 || i4 == 10 && i6 == CONST_CHI_12 || i4 == 11 && i6 == CONST_CHI_2 || i4 == 12 && i6 == CONST_CHI_4) {
            arrayList.add("Kim đường ")
        }
        if (i4 == 1 && i6 == CONST_CHI_8 || i4 == 2 && i6 == CONST_CHI_10 || i4 == 3 && i6 == CONST_CHI_12 || i4 == 4 && i6 == CONST_CHI_2 || i4 == 5 && i6 == CONST_CHI_4 || i4 == 6 && i6 == CONST_CHI_6 || i4 == 7 && i6 == CONST_CHI_8 || i4 == 8 && i6 == CONST_CHI_10 || i4 == 9 && i6 == CONST_CHI_12 || i4 == 10 && i6 == CONST_CHI_2 || i4 == 11 && i6 == CONST_CHI_4 || i4 == 12 && i6 == CONST_CHI_6) {
            arrayList.add("Ngọc đường ")
        }
        return join(arrayList, ", ")
    }

    private fun getInMillis(solarDay: Int, solarMonth: Int, solarYear: Int): Long {
        calendarInstance.set(solarYear, solarMonth - 1, solarDay)
        return calendarInstance.timeInMillis
    }

    private fun getTimeBetween2Dates(solarDay: Int, solarMonth: Int, solarYear: Int): Long {
        return (getInMillis(solarDay, solarMonth, solarYear) - (getInMillis(
            1995,
            1,
            1
        ) + 10000)) / 86400000
    }

    private fun getTimeBetween1(solarDay: Int, solarMonth: Int, solarYear: Int): Long {
        val getTimeBetween2Dates = (getTimeBetween2Dates(solarDay, solarMonth, solarYear) % 28) + 11
        if (getTimeBetween2Dates > 28) {
            return getTimeBetween2Dates - 28
        }
        return if (getTimeBetween2Dates <= Constant.INDEX_DEFAULT) getTimeBetween2Dates + 28 else getTimeBetween2Dates
    }

    fun getTimeBetween(solarDay: Int, solarMonth: Int, solarYear: Int): String {
        val getTimeBetween = getTimeBetween1(solarDay, solarMonth, solarYear).toInt()
        if (getTimeBetween < 1) {
            return "${arrListAdvice[Constant.INDEX_DEFAULT]} <br> ${arrListAdvice2[Constant.INDEX_DEFAULT]}"
        }
        val sb = java.lang.StringBuilder()
        sb.append(arrListAdvice[getTimeBetween - 1])
        sb.append("<br>")
        sb.append(arrListAdvice2[getTimeBetween - 1])
        return sb.toString()
    }

    fun getTimeGoodHour(solarDay: Int, monthSolar: Int, yearSoloar: Int): String {
        val str = GIO_HD[((jdn(solarDay, monthSolar, yearSoloar) + 1) % 12) % 6]
        var count = 0
        var str2 = EMPTY
        var count2 = 0
        while (count < 12) {
            var count3 = count + 1
            if (str.substring(count, count3) == "1") {
                val sb = java.lang.StringBuilder()
                sb.append(str2 + CHI[count])
                sb.append(" ")
                var count4 = count * 2
                sb.append((count4 + 23) % 24)
                sb.append("h - ")
                sb.append((count4 + 1) % 24)
                sb.append("h")
                var sb2 = sb.toString()
                ++count2
                if (count2 < 6) {
                    sb2+=", "
                }
                str2 = sb2
            }
            count = count3
        }
        return str2
    }

    fun getListGioHD(i: Int, i2: Int, i3: Int): ArrayList<HourGood> {
        val list = arrayListOf<HourGood>()
        val str = GIO_HD[(jdn(i, i2, i3) + 1) % 12 % 6]
        var i4 = 0
        while (i4 < 12) {
            val i5 = i4 + 1
            if (str.substring(i4, i5) == "1") {
                val sb = StringBuilder()
                val i6 = i4 * 2
                sb.append((i6 + 23) % 24)
                sb.append("-")
                sb.append((i6 + 1) % 24)
                sb.append("h")
                list.add(HourGood(icon = ICON_CHI[i4], name = CHI[i4], hour = sb.toString()))
            }
            i4 = i5
        }
        return list
    }
}