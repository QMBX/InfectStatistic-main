import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * 将命令类注册到工厂，由工厂根据命令行参数生成对应的类
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
    INFECTION, SUSPECTED, CURE, DEAD
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
    private static final String[] PROVINCES = {"安徽", "北京", "重庆", "福建", "甘肃", "广东", "广西", "贵州", "海南"
        , "河北", "河南", "黑龙江", "湖北", "湖南", "吉林", "江苏", "江西", "辽宁", "内蒙古", "宁夏", "青海", "山东", "山西"
        , "陕西", "上海", "四川", "天津", "西藏", "新疆", "云南", "浙江"};

    private String inputPath, outputPath;
    private LocalDate deadLine;
    private List<PATIENT_TYPE> showTypes = new ArrayList<>();
    private List<String> showProvinces = new ArrayList<>();

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
            boolean isMatch = false;

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
}