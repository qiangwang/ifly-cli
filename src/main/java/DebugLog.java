import java.text.SimpleDateFormat;

class DebugLog {
    private static boolean show = true;

    static void setShowLog(boolean show) {
        DebugLog.show = show;
    }

    static void log(String log) {
        if (!DebugLog.show) return;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(new java.util.Date());
        System.out.println("<" + date + ">" + log);
    }
}