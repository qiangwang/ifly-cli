import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.iflytek.cloud.speech.Setting;
import com.iflytek.cloud.speech.SpeechUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Cli {
    public static void main(String args[]) {
        JCommander.Builder builder = JCommander.newBuilder();
        HashMap<String, Cmd> cmdList = new HashMap<>();

        //添加子命令
        AudioToTextCommand c1 = new AudioToTextCommand();
        builder.addCommand(c1.id(), c1);
        cmdList.put(c1.id(), c1);

        JCommander jc = builder.build();
        jc.setProgramName("ifly-sdk");

        //解析命令行
        String cmdId;
        try {
            jc.parse(args);

            cmdId = jc.getParsedCommand();
            if (cmdId == null) throw new NullPointerException();

        } catch (NoSuchMethodError | Exception e) {
            jc.usage();
            System.exit(1);
            return;
        }

        //执行命令
        Cmd cmd = cmdList.get(cmdId);

        SpeechUtility.createUtility("appid=" + cmd.appId);
        boolean showLog = cmd.showLog.equals("true");
        Setting.setShowLog(showLog);
        DebugLog.setShowLog(showLog);

        cmd.run();
    }

    private static abstract class Cmd {
        @Parameter(names={"--appId"}, required = true, description = "讯飞APP_ID")
        private String appId;

        @Parameter(names={"--showLog"}, description = "显示详细日志")
        private String showLog = "true";

        abstract String id();
        abstract void run();
    }

    @Parameters(commandDescription = "语音转文字")
    private static class AudioToTextCommand extends Cmd {
        @Parameter(names={"-i"}, required = true, description = "pcm音频文件")
        private String inputPath;

        @Parameter(names={"-o"}, required = true, description = "文字输出文件")
        private String outputPath;

        @Parameter(names = "--help", help = true, description = "帮助信息")
        private boolean help;

        String id() {
            return "audioToText";
        }

        void run() {
            AudioToText att = new AudioToText();
            String text = att.transform(inputPath);
            try {
                Files.write(Paths.get(outputPath), text.getBytes());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}