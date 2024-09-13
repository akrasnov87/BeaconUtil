package com.mobwal.walker.beautil.v1;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

class StringUtil {
    public static final String NULL = "null";

    /**
     * Корректировка строки
     * @param txt входная строка
     * @return результат
     */
    public static String normalString(String txt) {
        if(txt == null) {
            return "";
        }
        return txt.equals(NULL) ? "" : txt;
    }

    /**
     * строка является пустой или равна null
     * @param input входная строка
     * @return результат сравнения
     */
    public static boolean isEmptyOrNull(String input) {
        String normal = normalString(input);
        return normal.isEmpty();
    }

    /**
     * Получение расширения файла
     *
     * @param name имя файла
     * @return расширение
     */
    @Nullable
    public static String getFileExtension(String name) {
        if (name != null && !name.isEmpty()) {
            int strLength = name.lastIndexOf(".");
            if (strLength >= 0) {
                String ext = name.substring(strLength + 1).toLowerCase();
                if (ext.isEmpty()) {
                    return null;
                } else {
                    return "." + ext;
                }
            }
        }

        return "";
    }

    /**
     * Очистка имени от расширения
     * @param name имя файла
     * @return результат
     */
    public static String getNameWithOutExtension(@NonNull String name) {
        if(TextUtils.isEmpty(name)) {
            return "";
        }

        String ext = getFileExtension(name);
        if(ext != null) {
            return name.replace(ext, "");
        }

        return "";
    }

    /**
     * Преобразование исключения в строку
     *
     * @param e исключение
     * @return строка
     */
    public static String exceptionToString(@NonNull Throwable e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }
    /**
     * Очистка начального и конечного символа
     * @param data строка для обработки
     * @param symbol символ для очистки
     * @return форматированная строка
     */
    public static String trimSymbol(@NonNull String data, char symbol) {
        int len = data.length();
        int st = 0;

        while ((st < len) && (data.charAt(st) <= symbol)) {
            st++;
        }
        while ((st < len) && (data.charAt(len - 1) <= symbol)) {
            len--;
        }
        return ((st > 0) || (len < data.length())) ? data.substring(st, len) : data;
    }

    /**
     * Создание рандомной строки определенной длины sizeOfRandomString
     * @param sizeOfRandomString длина рандомной строки
     * @return строка
     */
    public static String getRandomString(final int sizeOfRandomString) {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for(int i = 0;i < sizeOfRandomString; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Получение md5-хеш кода
     *
     * @param inputString входная строка
     * @return хеш-код
     */
    public static String md5(String inputString) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(inputString.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Заполение разделителями
     *
     * @param count     количество
     * @param separator разделитель
     * @return возвращается строка
     */
    public static String fullSpace(int count, String separator) {
        if (count > 0 && !isEmptyOrNull(separator)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                builder.append(separator);
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * Сравнение строк без учета регистра
     * @param str1 строка 1
     * @param str2 строка 2
     * @return результат сравнения
     */
    public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        } else if (str1.length() != str2.length()) {
            return false;
        } else {
            return regionMatches(str1, str2, str1.length());
        }
    }

    private static boolean regionMatches(final CharSequence cs,
                                         final CharSequence substring, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(true, 0, (String) substring, 0, length);
        }
        int index1 = 0;
        int index2 = 0;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Сокращение guid
     *
     * @param guid UUID
     * @return возвращается до символа -
     */
    public static String getShortGuid(String guid) {
        if (!isEmptyOrNull(guid) && guid.indexOf("-") > 0) {
            return guid.substring(0, guid.indexOf("-"));
        } else {
            return guid;
        }
    }
}
