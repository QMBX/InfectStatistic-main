# InfectStatistic-221701317
## 疫情统计
这是一个统计全国各区域某日感染情况的程序

## 如何运行？
将221701317\src目录下的两个java文件下载到你的电脑上，用你电脑上的javac程序对其编译，就可得到.class，再用java运行相应的.class文件即可运行本程序。

注意：在windows上编译运行时，需要在编译添加`-encoding=UTF-8`,在运行时添加`-D``file.encoding=UTF-8`才能正确运行。

#

描述你的项目，包括如何运行、功能简介、作业链接、博客链接等


## 功能简介

本程序可以根据你输入的路径，读取该路径下的日志文件，并在你指定的位置，输出统计结果

本程序当前版本仅支持list命令 支持以下命令行参数：

* `-log` 指定日志目录的位置，该项必会附带，请直接使用传入的路径，而不是自己设置路径
* `-out` 指定输出文件路径和文件名，该项必会附带，请直接使用传入的路径，而不是自己设置路径
* `-date` 指定日期，不设置则默认为所提供日志最新的一天。你需要确保你处理了指定日期之前的所有log文件
* `-type` 可选择[ip： infection patients 感染患者，sp： suspected patients 疑似患者，cure：治愈 ，dead：死亡患者]，使用缩写选择，如 `-type ip` 表示只列出感染患者的情况，`-type sp cure`则会按顺序【sp, cure】列出疑似患者和治愈患者的情况，不指定该项默认会列出所有情况。
* `-province` 指定列出的省，如`-province 福建`，则只列出福建，`-province 全国 浙江`则只会列出全国、浙江
注：`java InfectStatistic`表示执行主类`InfectStatistic`，`list`为<b>命令</b>，`-date`代表该命令附带的</b>参数</b>，`-date`后边跟着具体的<b>参数值</b>，如`2020-01-22`。`-type` 的多个参数值会用空格分离，每个命令参数都在上方给出了描述，每个命令都会携带一到多个命令参数

## 作业链接
 [寒假作业（2/2）](https://www.cnblogs.com/annualplant/p/12329106.html#header)

## 博客链接
 [一年生](https://www.cnblogs.com/annualplant/)