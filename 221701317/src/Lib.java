import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lib
 * @author zxx
 * @version 1.0
 */
public class Lib
{
    /**
     * 该函数为程序真正的入口。
     * 根据命令行参数，生成对应的命令类，然后执行命令类
     * @param args 命令行参数
     */
    void run(String[] args)
    {
        try
        {
            CommandArgs commandArgs = new CommandArgs(args);
            CommandFactory factory = new CommandFactory();
            AbstractCommand command = factory.getCommand(commandArgs);
            command.execute();
        }
        catch (MyExcepiton mex)
        {
            System.out.println(mex.getMessage());
        }

    }
}

/**
 * 命令类的接口，通过该接口方便对命令进行扩充
 * @author zxx
 * @version 1.0
 */
interface AbstractCommand
{
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
    void config(String parameter, String val) throws MyExcepiton;

    /**
     * 执行命令
     */
    void execute() throws MyExcepiton;
}

/**
 * 命令行参数对象
 * 将命令行参数和对命令行参数要用的几个操纵封装成一个对象
 * @author zxx
 * @version 1.0
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
    CommandArgs(String[] args) throws MyExcepiton
    {
        if (args.length <= 0)
            throw new MyExcepiton("程序缺少参数");
        else
        {
            int length = args.length;
            String parameter = DEFAULT_PARAMETER;

            commandName = args[0];
            for (int i = 1; i < length; i++)
            {
                if (args[i].startsWith("-"))
                {
                    if (parameters.get(parameter) == null && !DEFAULT_PARAMETER.equals(parameter))
                        throw new MyExcepiton("参数不正确");
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
            if (parameters.get(parameter) == null)
                throw new MyExcepiton("参数不正确");
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
    AbstractCommand getCommand(CommandArgs commandArgs) throws MyExcepiton
    {
        AbstractCommand abstractCommand = null;
        String commandName = commandArgs.getCommandName();

        if (LIST_COMMAND.equals(commandName))
        {
            abstractCommand = new ListCommand();
        }
        else
            throw new MyExcepiton("程序没有相应的命令");

        init(abstractCommand, commandArgs);

        return abstractCommand;
    }

    /**
     * 使用命令行对象对命令类进行初始化
     * @param abstractCommand 要初始化的命令类
     * @param commandArgs 使用到的命令行对象
     */
    private void init(AbstractCommand abstractCommand, CommandArgs commandArgs) throws MyExcepiton
    {
        String[] parameters = abstractCommand.getParameters();

        for (String parameter : parameters)
            if (commandArgs.hasParameter(parameter))
                abstractCommand.config(parameter, commandArgs.getValue(parameter));
    }
}

/**
 * 对病人数据的分类，同时建立枚举与分类的名称的映射
 * @author zxx
 * @version 1.0
 */
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

    /**
     * 获得枚举的位置，位置信息描述了存储一个区域中的病人数据时，数据类型按该位置排序
     * @return 返回当前类型在所有类型中的位置
     */
    int getValue()
    {
        return value;
    }

    /**
     * 获得该类型的名称
     * @return 该枚举类型对应的名称
     */
    String getName()
    {
        return name;
    }

}

/**
 * list命令类，实现了AbstractCommand接口
 * 当前将数据的统计和输出都集成在这个类中
 * @author zxx
 * @version 1.0
 */
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
        for (PATIENT_TYPE type : PATIENT_TYPE.values())
        {
            showTypes.add(type);
        }
    }

    @Override
    public String[] getParameters()
    {
        return PARMETERS;
    }

    @Override
    public void config(String parameter, String val) throws MyExcepiton
    {
        if (PARAMETER_LOG_PATH.equals(parameter))
        {
            File dir = new File(val);

            if (!dir.exists())
            {
                throw new MyExcepiton("日志文件夹不存在");
            }
            inputPath = val;
        }
        else if (PARAMETER_OUTPUT_FILE.equals(parameter))
        {
            outputPath = val;
        }
        else if (PARAMETER_DEADLINE.equals(parameter))
        {
            try
            {
                deadLine = LocalDate.parse(val);
            }
            catch(DateTimeParseException ex)
            {
                throw new MyExcepiton("日期参数不正确，应满足YYYY-MM-DD的格式");
            }

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
                    throw new MyExcepiton("输入参数有误");
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
                    throw new MyExcepiton("省份参数错误");
            }
        }
    }

    @Override
    public void execute() throws MyExcepiton
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

        //统计数据
        try
        {
            LogParser parser = null;
            List<ChangeArray>changes = null;

            for (String log : logs)
            {
                parser = new LogParser(inputPath, log);
                while((changes = parser.nextLine()) != null)
                {
                    for (ChangeArray change: changes)
                    {
                        String province = change.getProvince();
                        int index = 0;
                        for (; index < PROVINCES.length; index++)
                        {
                            if (PROVINCES[index].equals(province))
                                break;
                        }
                        if (index == PROVINCES.length)
                        {
                            throw new MyExcepiton("出现未知省份");
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
            throw new MyExcepiton("日志文件不存在");
        }
        catch (IOException ex)
        {
            throw new MyExcepiton("日志文件读取出错");
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
            throw new MyExcepiton("输出文件夹创建失败");
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

    /**
     * 获得命令参数中inputPath对应的目录下的所有满足日志格式，并在date参数指定的日期之前的日志文件
     * @return log参数值的目录下，在date指定的日期之前（包括指定日期）的所有日志名称数组
     */
    private List<String> getLogFile() throws MyExcepiton
    {
        File dir = new File(inputPath);
        String[] names = dir.list();
        List<String>logs = new ArrayList<>();
        String DATA_FORMAT = "^\\d{4}-\\d{2}-\\d{2}.log.txt$";

        for (int i = 0 ; i < names.length; i++)
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
            throw  new MyExcepiton("不存在日志文件");
        Collections.sort(logs);
        return logs;
    }

    /**
     * 按 《省》 数据类型人数 的格式输出数据
     * @param provienceName 输出的数据对应的区域名
     * @param paintsNum 要输出的数据
     */
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

/**
 * 该类存放某个省某类病人人数变化的数据
 * 该类与日志文件中的记载行是多对一的。
 * @author zxx
 * @version 1.0
 */
class ChangeArray
{
    String province;
    PATIENT_TYPE type;
    int num;

    /**
     * 私有的构造方法，避免数据赋值的情况
     */
    private ChangeArray(){}

    /**
     * 由该静态方法产生存放数据的ChaangeArray类
     * @param province 数据发生变化的区域名称
     * @param type 发生变化的数据类型
     * @param num 数据变化了多少。可正可负，将变化统计时，该数以加法加入总数
     * @return 返回存储数据的ChangeArray
     */
    static ChangeArray changeOf(String province, PATIENT_TYPE type, int num)
    {
        ChangeArray increase = new ChangeArray();

        increase.province = province;
        increase.type = type;
        increase.num = num;

        return increase;
    }

    /**
     * 获得该类中存储的省份名称
     * @return 省份名称
     */
    String getProvince()
    {
        return province;
    }
    /**
     * 获得该类中存储的数据类型
     * @return 数据类型
     */
    PATIENT_TYPE getType()
    {
        return type;
    }
    /**
     * 获得该类中存储的变化数量
     * @return 变化数量
     */
    int getNum()
    {
        return num;
    }
}

/**
 * 日志处理类，读取一个日志文件中的每一行，并让LogLine链去处理行
 * @author zxx
 * @#version 1.0
 */
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

    /**
     * 初始化处理器的参数
     * @param path 要读取的文件所在文件夹
     * @param name 要读取的文件名
     * @throws FileNotFoundException 如果找不到文件，将抛出该异常
     */
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

    /**
     *
     /**
     * 从第一行开始，每调用一次，返回下一行数据。
     * @return 之前读取行的下一行。如果到达文件结尾，则返回null并关闭打开的文件
     * @throws IOException 如果读写器已经关闭后还执行该方法，或者读取下一行数据时出现错误，则抛出该异常
     * @throws MyExcepiton 如果出现责任链不能处理的行，将抛出MyException
     */
    List<ChangeArray> nextLine() throws IOException, MyExcepiton
    {
        String line;

        line = dataInput.readLine();
        if (line != null)
        {
            return commentLine.parseLine(line);
        }
        else
        {
            dataInput.close();
            return null;
        }
    }


}

/**
 * 日志行处理类的统一接口
 * 目前实现责任链模式的方法还可以优化，可以用类的继承来替代接口的使用
 * @author zxx
 * @version 1.0
 */
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
    List<ChangeArray> parseLine(String line) throws MyExcepiton;
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
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
    public List<ChangeArray> parseLine(String line) throws MyExcepiton
    {
        throw new MyExcepiton("出现一个意料之外的行\n" + line);
    }
}

class MyExcepiton extends Exception
{
    private String message;

    public MyExcepiton(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

}