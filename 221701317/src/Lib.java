import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void run(String[] args)
    {
        System.out.println("enter Lib class in run");
        CommandArgs commandArgs = new CommandArgs(args);
        System.out.println(commandArgs.getValue("log"));
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
class CommandArgs {


    //存储输入的各个参数与对应的值
    private Map<String, String> parameters = new HashMap<>();
    //存储命令的值
    private String commandName;

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
            String parameter = "default";

            commandName = args[0];
            for(int i = 1; i < length; i++)
            {
                if (args[i].startsWith("-"))
                {
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
        }
    }

    /**
     * 返回本次执行的命令名称
     * @return 本次执行的命令名称
     */
    public String getCommand()
    {
        return commandName;
    }

    /**
     * 判断命令行参数中是否有对应参数
     * @param parameter 参数
     * @return 如果命令行中有出现参数，返回true,否则返回false
     */
    public boolean hasParamerter(String parameter)
    {
        return parameters.containsKey(parameter);
    }

    /**
     * 获取参数的值
     * @param parameter 要获取的参数
     * @return 如果参数在命令行参数中有值，则返回对应的值，否则返回null
     */
    public String getValue(String parameter)
    {
        return parameters.get(parameter);
    }
}