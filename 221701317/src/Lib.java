import junit.framework.TestCase;
import org.junit.Test;

import javax.xml.stream.events.Comment;
import java.io.*;
import java.sql.SQLOutput;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lib
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since 0.1
 */
public class Lib
{
    void run(String[] args)
    {
        System.out.println("enter Lib class in run");

        CommandArgs commandArgs = new CommandArgs(args);
        CommandFactory factory = new CommandFactory();
        AbstractCommand command = factory.getCommand(commandArgs);
        command.execute();
    }
}

interface AbstractCommand
{
    /**
     * 获得当前命令的名称
     * @return 返回当前命令的名称.
     */
    String getCommandName();

    /**
     * 获取命令所需的参数
     * @return 命令需要的参数
     */
    String[] getParameters();

    /**
     * 向命令对象传入对应参数
     * @param parameter 传入的参数
     * @param val 参数的值
     */
    void config(String parameter, String val);

    /**
     * 执行命令
     */
    void execute();
}

/**
 * 命令行参数对象
 * 将命令行参数和对命令行参数要用的几个操纵封装成一个对象
 * @version 0.1
 */
class CommandArgs
{
    //存储输入的各个参数与对应的值
    private Map<String, String> parameters = new HashMap<>();
    //存储命令的值
    private String commandName;
    //当命令有自己的参数值时，采用的参数
    private static final String DEFAULT_PARAMETER = "default";
    /**
     * 将命令行参数转化成参数到值的映射，默认每次运行只有一个命令
     * @param args 程序读取到的命令行参数
     */
    CommandArgs(String[] args)
    {
        if (args.length <= 0)
        {
            //TODO： 此处需改为异常抛出
            System.out.println("程序缺少参数");
        }
        else
        {
            int length = args.length;
            String parameter = DEFAULT_PARAMETER;

            commandName = args[0];
            for (int i = 1; i < length; i++)
            {
                if (args[i].startsWith("-"))
                {
                    //TODO 此处改为参数异常抛出
                    if (parameters.get(parameter) == null)
                    {
                        System.out.println("参数不正确");
                    }
                    parameter = args[i].substring(1);
                }
                else
                {
                    if (parameters.containsKey(parameter))
                        parameters.put(parameter, parameters.get(parameter) + " " + args[i]);
                    else
                        parameters.put(parameter, args[i]);
                }
            }
            //TODO 此处改为参数异常抛出
            if (parameters.get(parameter) == null)
            {
                System.out.println("参数不正确");
            }
        }
    }

    /**
     * 返回本次执行的命令名称
     * @return 本次执行的命令名称
     */
    String getCommandName()
    {
        return commandName;
    }

    /**
     * 判断命令行参数中是否有对应参数
     * @param parameter 参数
     * @return 如果命令行中有出现参数，返回true,否则返回false
     */
    boolean hasParameter(String parameter)
    {
        return parameters.containsKey(parameter);
    }

    /**
     * 获取参数的值
     * @param parameter 要获取的参数
     * @return 如果参数在命令行参数中有值，则返回对应的值，否则返回null
     */
    String getValue(String parameter)
    {
        return parameters.get(parameter);
    }
}

/**
 * 命令类工厂，根据命令行参数生成对应的类
 * 程序的所有命令类需要添加到该类的getCommand方法中
 */
class CommandFactory
{
    private static final String LIST_COMMAND = "list";

    /**
     * 通过命令行对象，生成并初始化对应的命令类
     * @param commandArgs 使用到的命令行对象
     * @return 初始化后的命令类
     */
    AbstractCommand getCommand(CommandArgs commandArgs)
    {
        AbstractCommand abstractCommand = null;
        String commandName = commandArgs.getCommandName();

        if (LIST_COMMAND.equals(commandName))
        {
            abstractCommand = new ListCommand();
        }
        else
        {
            //TODO: 此处应抛出没有相应命令的异常
            System.out.println("程序没有相应命令");
        }
        init(abstractCommand, commandArgs);

        return abstractCommand;
    }

    /**
     * 使用命令行对象对命令类进行初始化
     * @param abstractCommand 要初始化的命令类
     * @param commandArgs 使用到的命令行对象
     */
    private void init(AbstractCommand abstractCommand, CommandArgs commandArgs)
    {
        String[] parameters = abstractCommand.getParameters();

        for (int i = 0; i < parameters.length; i++)
            if (commandArgs.hasParameter(parameters[i]))
                abstractCommand.config(parameters[i], commandArgs.getValue(parameters[i]));
    }
}

enum PATIENT_TYPE
{
    INFECTION(0,"感染患者"), SUSPECTED(1, "疑似患者"), CURE(2, "治愈"), DEAD(3, "死亡");

    private int value;
    private String name;
    PATIENT_TYPE(int value, String name)
    {
        this.value = value;
        this.name = name;
    }


    int getValue()
    {
        return value;
    }
    String getName()
    {
        return name;
    }

}

class ListCommand implements AbstractCommand
{
    private static final String COMMAND_NAME = "list";
    private static final String PARAMETER_LOG_PATH = "log";
    private static final String PARAMETER_OUTPUT_FILE = "out";
    private static final String PARAMETER_DEADLINE = "date";
    private static final String PARAMETER_TYPE = "type";
    private static final String INFECTION_PATIENTS = "ip";
    private static final String SUSPECTED_PATIENTS = "sp";
    private static final String CURE_PATIENTS = "cure";
    private static final String DEAD_PATIENTS = "dead";
    private static final String PARAMETER_PROVINCES = "province";
    private static final String[] PARMETERS = {PARAMETER_LOG_PATH, PARAMETER_OUTPUT_FILE, PARAMETER_DEADLINE
        , PARAMETER_TYPE, PARAMETER_PROVINCES};
    private static final String[] PROVINCES = {"全国","安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南"
        , "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西"
        , "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江"};

    private String inputPath, outputPath;
    private LocalDate deadLine;
    private List<PATIENT_TYPE> showTypes = new ArrayList<>();
    private List<String> showProvinces = new ArrayList<>();

    /**
     * 初始化命令参数值
     */
    ListCommand()
    {
        for(PATIENT_TYPE type : PATIENT_TYPE.values())
        {
            showTypes.add(type);
        }
    }

    @Override
    public String getCommandName()
    {
        return COMMAND_NAME;
    }

    @Override
    public String[] getParameters()
    {
        return PARMETERS;
    }

    @Override
    public void config(String parameter, String val)
    {
        if (PARAMETER_LOG_PATH.equals(parameter))
        {
            File dir = new File(val);

            if (!dir.exists())
            {
                //TODO: 改为抛出路径不存在的异常
                System.out.println("日志文件夹不存在");
            }
            inputPath = val;
        }
        else if (PARAMETER_OUTPUT_FILE.equals(parameter))
        {
            outputPath = val;
        }
        else if (PARAMETER_DEADLINE.equals(parameter))
        {
            /*
            TODO: 这里将会对日期格式进行检验，日期格式由LocalDate类内置指定，为“XXXX-XX-XX”
                如果日期出现错误，将会抛出java.time.format.DateTimeParseException错误，需要在对应位置进行处理
            */
            deadLine = LocalDate.parse(val);
    }
        else if (PARAMETER_TYPE.equals(parameter))
        {
            String[] types = val.split(" ");
            showTypes.clear();
            for (int i = 0 ; i < types.length; i++)
            {
                if (INFECTION_PATIENTS.equals(types[i]))
                    showTypes.add(PATIENT_TYPE.INFECTION);
                else if (SUSPECTED_PATIENTS.equals(types[i]))
                    showTypes.add(PATIENT_TYPE.SUSPECTED);
                else if (CURE_PATIENTS.equals(types[i]))
                    showTypes.add(PATIENT_TYPE.CURE);
                else if (DEAD_PATIENTS.equals(types[i]))
                    showTypes.add(PATIENT_TYPE.DEAD);
                else
                {
                    //TODO 这里应该抛出参数错误的异常
                    System.out.println("输入参数有误");
                }
            }
        }
        else if (PARAMETER_PROVINCES.equals(parameter))
        {
            String[] provinces = val.split(" ");
            boolean isMatch;

            for (int i = 0; i < provinces.length; i++)
            {
                isMatch = false;
                for (int j = 0; j < PROVINCES.length; j++)
                {
                    if (provinces[i].equals(PROVINCES[j]))
                    {
                        showProvinces.add(PROVINCES[j]);
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch)
                {
                    //TODO: 此处应抛出省份参数错误的异常
                    System.out.println("省份参数错误");
                }
            }
        }
    }

    @Override
    public void execute()
    {
        List<String>logs = getLogFile();

        int[][] patients = new int[PROVINCES.length][4];
        boolean[] isChange = new boolean[PROVINCES.length];

        for (int i = 0; i < patients.length; i++)
        {
            for (int j = 0; j < patients[i].length; j++)
                patients[i][j] = 0;
            isChange[i] = false;
        }
        isChange[0] = true;

        //读取数据
        try
        {
            LogParser parser = null;
            List<ChangeArray>changes = null;

            for (String log : logs)
            {
                parser = new LogParser(inputPath, log);
                while((changes = parser.nextLine()) != null)
                {
                    for(ChangeArray change: changes)
                    {
                        String province = change.getProvince();
                        int index = 0;
                        for(; index < PROVINCES.length; index++)
                        {
                            if (PROVINCES[index].equals(province))
                                break;
                        }
                        if (index == PROVINCES.length)
                        {
                            //TODO: 此处抛出出现未知省份的异常
                            System.out.println("出现未知省份");
                        }

                        if (!isChange[index])
                            isChange[index] = true;
                        patients[index][change.getType().getValue()] += change.getNum();
                        patients[0][change.getType().getValue()] += change.getNum();
                    }
                }
            }
        }
        catch (FileNotFoundException ex)
        {
            //TODO： 此处应抛出日志文件不存在的异常
            System.out.println("日志文件不存在");
        }
        catch (IOException ex)
        {
            //TODO: 此处应抛出日志文件读取出错的异常
            System.out.println("日志文件读取出错");
        }

        //输出数据
        try {
            BufferedWriter dataOutput
                    = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputPath)));
            int[] sum = new int[patients[0].length];
            for (int i : sum)
                i = 0;


            if (showProvinces != null && showProvinces.size() > 0)
            {
                for (String showProvience : showProvinces)
                    for (int i = 0; i < PROVINCES.length; i++)
                        if (PROVINCES[i].equals(showProvience))
                            printPaint(showProvience, patients[i]);
            }
            else
            {
                for (int i = 0; i < PROVINCES.length; i++)
                {
                    if (isChange[i])
                    {
                        printPaint(PROVINCES[i], patients[i]);
                    }
                }
            }

        }
        catch (FileNotFoundException ex)
        {
            //TODO： 此处应抛出文件不存在异常
            System.out.println("输出文件创建失败");
        }

        /* 以下代码用于检测命令类是否配置成功
        System.out.println("inputPath: " + inputPath);
        System.out.println("outputPath: " + outputPath);
        System.out.println(deadLine);
        for (int i = 0; i < showTypes.size(); i++)
            System.out.println(showTypes.get(i));
        for (int i = 0; i < showProvinces.size(); i++)
            System.out.println(showProvinces.get(i));
        */
    }

    private List<String> getLogFile()
    {
        File dir = new File(inputPath);
        String[] names = dir.list();
        List<String>logs = new ArrayList<>();
        String DATA_FORMAT = "^\\d{4}-\\d{2}-\\d{2}.log.txt$";

        for(int i = 0 ; i < names.length; i++)
            if (Pattern.matches(DATA_FORMAT, names[i]))
            {
                try
                {
                    LocalDate logtime = LocalDate.parse(names[i].substring(0,10));
                    if (deadLine == null || deadLine.isAfter(logtime) || deadLine.isEqual(logtime))
                    {
                        logs.add(names[i]);
                    }
                }
                catch (DateTimeParseException ex) {}
            }

        if (logs.size() <= 0)
        {
            //TODO: 此处应抛出目录下不存在日志文件的异常
            System.out.println("不存在日志文件");
        }
        Collections.sort(logs);
        return logs;
    }

    private void printPaint(String provienceName,int[] paintsNum)
    {
        System.out.print(provienceName);
        for (PATIENT_TYPE type : showTypes)
        {
            System.out.print(" ");
            System.out.print(type.getName());
            System.out.print(paintsNum[type.getValue()]+"人");
        }
        System.out.println();
    }
}


class ChangeArray
{
    String province;
    PATIENT_TYPE type;
    int num;

    private ChangeArray(){}

    static ChangeArray changeOf(String province, PATIENT_TYPE type, int num)
    {
        ChangeArray increase = new ChangeArray();

        increase.province = province;
        increase.type = type;
        increase.num = num;

        return increase;
    }
    String getProvince()
    {
        return province;
    }
    PATIENT_TYPE getType()
    {
        return type;
    }
    int getNum()
    {
        return num;
    }
}


class LogParser
{
    String logName, path;
    BufferedReader dataInput;
    private CommentLine commentLine;
    private IncreaseInfectionPatientLine increaseInfectionPatientLine;
    private IncreaseSuspectedPatientLine increaseSuspectedPatientLine;
    private ChangeInfectionPatientLine changeInfectionPatientLine;
    private ChangeSuspectedPatientLine changeSuspectedPatientLine;
    private IncreaseDeadPatientLine increaseDeadPatientLine;
    private IncreaseCurePatientLine increaseCurePatientLine;
    private ComfireInfectionPatientLine comfireInfectionPatientLine;
    private ExcludeSuspectedPatientLine excludeSuspectedPatientLine;
    private UnexpectedLine unexpectedLine;
    LogParser(String path, String name) throws FileNotFoundException
    {
        this.path = path;
        this.logName = name;

        dataInput = new BufferedReader(new InputStreamReader(new FileInputStream(path + "\\" + logName)));

        commentLine = new CommentLine();
        increaseInfectionPatientLine = new IncreaseInfectionPatientLine();
        increaseSuspectedPatientLine = new IncreaseSuspectedPatientLine();
        changeInfectionPatientLine = new ChangeInfectionPatientLine();
        changeSuspectedPatientLine = new ChangeSuspectedPatientLine();
        increaseDeadPatientLine = new IncreaseDeadPatientLine();
        increaseCurePatientLine = new IncreaseCurePatientLine();
        comfireInfectionPatientLine = new ComfireInfectionPatientLine();
        excludeSuspectedPatientLine = new ExcludeSuspectedPatientLine();
        unexpectedLine = new UnexpectedLine();

        commentLine.setNextLogLine(increaseInfectionPatientLine);
        increaseInfectionPatientLine.setNextLogLine(increaseSuspectedPatientLine);
        increaseSuspectedPatientLine.setNextLogLine(changeInfectionPatientLine);
        changeInfectionPatientLine.setNextLogLine(changeSuspectedPatientLine);
        changeSuspectedPatientLine.setNextLogLine(increaseDeadPatientLine);
        increaseDeadPatientLine.setNextLogLine(increaseCurePatientLine);
        increaseCurePatientLine.setNextLogLine(comfireInfectionPatientLine);
        comfireInfectionPatientLine.setNextLogLine(excludeSuspectedPatientLine);
        excludeSuspectedPatientLine.setNextLogLine(unexpectedLine);
    }

    List<ChangeArray> nextLine() throws IOException
    {
        String line;

        line = dataInput.readLine();
        if (line != null)
        {
            return commentLine.parseLine(line);
        }
        else
            return null;
    }


    void close() throws IOException
    {
        dataInput.close();
    }


}

interface LogLine
{
    /**
     * 设置下一个行处理对象
     * @param logLine
     */
    void setNextLogLine(LogLine logLine);

    /**
     * 对行进行处理，如果处理失败，则传递给下一个行处理对象
     * @param line
     * @return
     */
    List<ChangeArray> parseLine(String line);
}

/**
 * 对应行格式：以//开头的注释或空行
 */
class CommentLine implements LogLine
{
    private static final String PATTERN = "^//.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if ("".equals(line) || Pattern.matches(PATTERN, line))
            return null;
        else
            return nextLogLine.parseLine(line);
    }
}

/**
 * 对应行格式：<省> 新增 感染患者 n人
 */
class IncreaseInfectionPatientLine implements LogLine
{
    private static final String PATTERN = ".*新增 感染患者.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[3]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.INFECTION, num);
            result.add(change);

            return result;
        }
        else
             return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省> 新增 疑似患者 n人
 */
class IncreaseSuspectedPatientLine implements LogLine
{
    private static final String PATTERN = ".*新增 疑似患者.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[3]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.SUSPECTED, num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省1> 感染患者 流入 <省2> n人
 */
class ChangeInfectionPatientLine implements LogLine
{
    private static final String PATTERN = ".*感染患者 流入.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[4]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.INFECTION, -num);
            result.add(change);
            change = ChangeArray.changeOf(words[3],PATIENT_TYPE.INFECTION, num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省1> 疑似患者 流入 <省2> n人
 */
class ChangeSuspectedPatientLine implements LogLine
{
    private static final String PATTERN = ".*疑似患者 流入.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[4]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.SUSPECTED, -num);
            result.add(change);
            change = ChangeArray.changeOf(words[3],PATIENT_TYPE.SUSPECTED, num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);
    }
}

/**
 * 对应行格式：<省> 死亡 n人
 */
class IncreaseDeadPatientLine implements LogLine
{
    private static final String PATTERN = ".*死亡.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[2]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.DEAD, num);
            result.add(change);
            change = ChangeArray.changeOf(words[0], PATIENT_TYPE.INFECTION, -num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省> 治愈 n人
 */
class IncreaseCurePatientLine implements LogLine
{
    private static final String PATTERN = ".*治愈.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[2]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.CURE, num);
            result.add(change);
            change = ChangeArray.changeOf(words[0],PATIENT_TYPE.INFECTION, -num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省> 疑似患者 确诊感染 n人
 */
class ComfireInfectionPatientLine implements LogLine
{
    private static final String PATTERN = ".*疑似患者 确诊感染.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[3]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0], PATIENT_TYPE.INFECTION, num);
            result.add(change);
            change = ChangeArray.changeOf(words[0],PATIENT_TYPE.SUSPECTED, -num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);

    }
}

/**
 * 对应行格式：<省> 排除 疑似患者 n人
 */
class ExcludeSuspectedPatientLine implements LogLine
{
    private static final String PATTERN = ".*排除 疑似患者.*";
    private LogLine nextLogLine;

    @Override
    public void setNextLogLine(LogLine logLine)
    {
        nextLogLine = logLine;
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        if (Pattern.matches(PATTERN, line))
        {
            List<ChangeArray> result = new ArrayList<>();
            String[] words = line.split(" ");

            String regEx="[^0-9]";
            Pattern p = Pattern.compile(regEx);
            Matcher m = p.matcher(words[3]);
            int num = Integer.parseInt(m.replaceAll(""));
            ChangeArray change = ChangeArray.changeOf(words[0],PATIENT_TYPE.SUSPECTED, -num);
            result.add(change);

            return result;
        }
        else
            return nextLogLine.parseLine(line);


    }
}

/**
 * 对应行格式：在之前都没有出现的行,对该行解析将抛出异常
 */
class UnexpectedLine implements LogLine
{
    @Override
    public void setNextLogLine(LogLine logLine)
    {
    }

    @Override
    public List<ChangeArray> parseLine(String line)
    {
        //TODO: 抛出一个意料之外的行的异常
        System.out.println("出现一个意料之外的行");
        System.out.println(line);
        return null;
    }
}