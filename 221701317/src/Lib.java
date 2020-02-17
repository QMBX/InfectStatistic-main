import java.util.List;

/**
 * Lib
 * TODO
 *
 * @author xxx
 * @version xxx
 * @since xxx
 */
public class Lib
{
    public void run(String args[])
    {

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
     * @return
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