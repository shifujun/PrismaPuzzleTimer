PrismaPuzzleTimer
=================

克隆PrismaPuzzleTimer0.6版本，重构为Maven项目。此0.6版本标记为tag:v0.6了。

Prisma Puzzle Timer的官方发布帖是http://www.speedsolving.com/forum/showthread.php?25790-Puzzle-Timer
原作者的项目开源在https://bitbucket.org/walter/puzzle-timer 。

由于Prisma Puzzle Timer目前（2014年10月22日）已停留在0.6版本上一年多了，我想有些地方可以自己改进了。

首先从https://bitbucket.org/walter/ 上把Puzzle Timer和prisma-graphics两个项目克隆下来。prisma-graphics其实很小，就几个类，不知道原作者为什么把它单独放在一个项目里了。我将它合并到Puzzle Timer项目里，然后重构为Maven项目。重构中将资源目录位置调整了，使之符合Maven项目一般的习惯。相应地修改了源码中引用资源文件时的路径。原项目依赖了3个jar格式的库，其中一个就是prisma-graphics，另外两个已修改为Maven项目的pom依赖方式。项目中不再有jar文件。

我自己是用Netbeans 8.0编译的，但由于已经重构为Maven项目了，所以应该其他支持Maven项目的IDE都能直接编译。

最近我在练五魔，所以根据自己需要对PPT做了些修改：打乱公式排整齐了点，字号大了点，五魔配色内置了大雁五魔常见的配色，加入了别人的汉化。如下图：
![image](https://github.com/shifujun/PrismaPuzzleTimer/raw/master/screenshots/magaminx.png)