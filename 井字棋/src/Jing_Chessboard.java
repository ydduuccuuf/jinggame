import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
public class Jing_Chessboard implements ActionListener {
    //窗口初始化
    JFrame frame = new JFrame("井字棋");
    JMenuBar bar = new JMenuBar();
    JMenu menu_choice = new JMenu("选项");
    JMenu menu_FirstOrLast = new JMenu("先后手");
    JMenu menu_difficulty = new JMenu("难度");
    JMenu menu_range = new JMenu("大小");
    JMenuItem item_begin = new JMenuItem("开始");
    JMenuItem item_reset = new JMenuItem("重来");
    JMenuItem item_save=new JMenuItem("保存结果");
    JMenuItem item_first = new JMenuItem("玩家先");
    JMenuItem item_last = new JMenuItem("电脑先");
    JMenuItem item_high = new JMenuItem("高难度");
    JMenuItem item_low = new JMenuItem("中难度");
    JMenuItem item_middle = new JMenuItem("普通难度");
    JMenuItem item_three = new JMenuItem("3*3");
    JMenuItem item_four = new JMenuItem("4*4");
    JMenuItem item_five = new JMenuItem("5*5");
    JMenuItem item_six = new JMenuItem("6*6");
    JMenuItem item_seven = new JMenuItem("7*7");
    JMenuItem item_eight = new JMenuItem("8*8");
    JMenuItem item_nine = new JMenuItem("9*9");
    JMenuItem item_ten = new JMenuItem("10*10");
    Container container = new Container();

    //游戏的数据结构
    int row =20;//定义行
    int col =20;//定义列
    final int MAX = 2000;//
    final int MIN = -2000;
    int depth;//定义搜索树的深度，也即调整电脑的难度
    int first_or_last;//定义先后手,默认玩家先（玩家0，电脑1）
    int player;//定义哪一方下棋，1为电脑，-1为玩家
    JButton[][] board_button = new JButton[row][col];//定义九个宫格，玩家下棋为圈，电脑下棋为叉
    int[][] board_value = new int[row][col];//定义九宫格对应的value，玩家下棋为-1，电脑下棋为1，未下棋为0
    int best_x,best_y;//电脑通过极大极小值法找到对自己最有利的一步
    int computer_first;//判断是否是电脑第一步，是为1，不是为0
    int computer_x;
    int computer_y;
    //alpha-beta剪枝算法
    int greed_flag=1;
    public Jing_Chessboard(){
        //显示窗口
        frame.setBounds(350,120,350,350);//设定大小
        //frame.setResizable(false);//不能改变大小
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//关闭行为
        frame.setLayout(new BorderLayout());//BorderLayout布局

        //添加菜单栏
        menu_choice.add(item_begin);
        menu_choice.add(item_reset);
        menu_choice.add(item_save);
        menu_FirstOrLast.add(item_first);
        menu_FirstOrLast.add(item_last);
        menu_difficulty.add(item_high);
        menu_difficulty.add(item_low);
        menu_difficulty.add(item_middle);
        menu_range.add(item_three);
        menu_range.add(item_four);
        menu_range.add(item_five);
        menu_range.add(item_six);
        menu_range.add(item_seven);
        menu_range.add(item_eight);
        menu_range.add(item_nine);
        menu_range.add(item_ten);
        bar.add(menu_choice);
        bar.add(menu_FirstOrLast);
        bar.add(menu_difficulty);
        bar.add(menu_range);
        frame.add(bar,BorderLayout.NORTH);

        //给菜单的选项加监听事件
        //开始
        item_begin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    for (int i = 0; i < row; i++) {
                        for (int j = 0; j < col; j++) {
                            board_value[i][j] = 0;
                            board_button[i][j].setEnabled(true);//按钮重置可以点开
                            board_button[i][j].setText("");
                        }
                    }
                    if (first_or_last == 1) {
                        computer_first = 1;
                        if (depth == 0) {
                            computer_play_normal(board_value);
                        } else {
                            computer_play(board_value);
                        }

                        computer_first = 0;
                    }
                }catch(Exception a)
                {
                    //a.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "请设置大小");
                }
            }
        });
        //重来
        item_reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < row; i++){
                    for(int j = 0; j < col; j++){
                        board_button[i][j].setEnabled(false);//按钮重置可以点开
                        board_button[i][j].setText("");
                    }
                }
            }
        });
        //保存
        item_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = "myText.txt";
                File 电焊人 = new File(fileName);
                try {
                    if (!电焊人.exists()) {
                        电焊人.createNewFile();
                    }
                    FileWriter 波奇塔还是波奇酱 = new FileWriter(电焊人,true);
                    if(is_win(board_value)==1)
                    {
                        波奇塔还是波奇酱.write("电脑赢\n");
                    } else if (is_win(board_value)==-1) {
                        波奇塔还是波奇酱.write("玩家赢\n");
                    }
                    else {
                        波奇塔还是波奇酱.write("输赢未定\n");
                    }
                    波奇塔还是波奇酱.close();
                } catch (IOException 呵_哈_什么嘛_我的枪法还挺准的嘛) {
                    呵_哈_什么嘛_我的枪法还挺准的嘛.printStackTrace();

                }
            }
        });
        item_first.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                first_or_last = 0;
            }
        });
        item_last.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                first_or_last = 1;
            }
        });
        item_high.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                depth = 4;
            }
        });
        item_low.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                depth = 2;
            }
        });
        item_middle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                depth = 0;
            }
        });
        item_three.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=3;
                col=3;
                menu_range.setEnabled(false);
                setrrange(0);

            }
        });
        item_four.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=4;
                col=4;
                menu_range.setEnabled(false);
                setrrange(1);
            }
        });
        item_five.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=5;
                col=5;
                menu_range.setEnabled(false);
                setrrange(2);
            }
        });
        item_six.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=6;
                col=6;
                menu_range.setEnabled(false);
                setrrange(3);
            }
        });
        item_seven.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=7;
                col=7;
                menu_range.setEnabled(false);
                setrrange(4);
            }
        });
        item_eight.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=8;
                col=8;
                menu_range.setEnabled(false);
                setrrange(5);
            }
        });
        item_nine.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=9;
                col=9;
                menu_range.setEnabled(false);
                setrrange(6);
            }
        });
        item_ten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                row=10;
                col=10;
                menu_range.setEnabled(false);
                setrrange(7);
            }
        });
        //添加container容器，放一系列按钮
        //初始化
        depth = 0;
        first_or_last = 0;
        best_x = 1;
        best_y = 1;
        computer_first = 0;
        computer_x = -1;
        computer_y = -1;
        frame.setVisible(true);//显示出来
    }
    //自定义棋盘大小
    void setrrange(int 肯德基疯狂星期四V我50谢谢)
    {
        frame.add(container, BorderLayout.CENTER);//加入容器放在CENTER布局位置
        container.setLayout(new GridLayout(row,col));//向container添加Grid网格布局
        for(int i = 0; i < col; i++){
            for(int j = 0; j < row; j++){
                board_value[i][j] = 0;
                JButton 星野爱 = new JButton();//初始化当前按钮
                board_button[i][j] = 星野爱;
                board_button[i][j].setOpaque(true);//设置按钮初始不可见
                board_button[i][j].setIcon(new ImageIcon("/white.png"));
                board_button[i][j].setEnabled(false);//按钮重置可以点开
                board_button[i][j].addActionListener(this);
                container.add(board_button[i][j]);//将button放到容器里面
            }
        }
    }
    //电脑下棋，中、高难度：博弈DP+贪心
    void computer_play(int[][] 喵喵喵_帕斯){

        if(GREEDY(喵喵喵_帕斯)==1)
        {
                computer_x = best_x;
                computer_y = best_y;
        }
        else {
            MAX_MIN(depth, -1000, 1000, 喵喵喵_帕斯);
            if (computer_first == 1) {
                best_x=row/2;
                best_y=row/2;
                computer_x = best_x;
                computer_y = best_y;
            //    computer_first=0;
            }
        }
        board_value[best_x][best_y] = 1;
        board_button[best_x][best_y].setText("X");//new ImageIcon("./cha.png"
        board_button[best_x][best_y].setEnabled(false);
    }

    //电脑下棋，普通难度，随机+贪心
    void computer_play_normal(int[][] 石蒜反冲){
        int a = 1,锦木千束 = 0,井上泷奈 = 0;
        while(a == 1){

            if(GREEDY(石蒜反冲)==1)
            {
                    computer_x = best_x;
                    computer_y = best_y;
                    break;
            }
            锦木千束 = (int)(Math.random() * col);
            井上泷奈 = (int)(Math.random() * row);
            if(石蒜反冲[锦木千束][井上泷奈] == 0){
                best_x = 锦木千束;
                best_y = 井上泷奈;
                //if(computer_first == 1){
                    computer_x = 锦木千束;
                    computer_y = 井上泷奈;
                //}
                break;
            }
        }
        board_value[best_x][best_y] = 1;
        board_button[best_x][best_y].setText("X");//new ImageIcon("./cha.png")
        board_button[best_x][best_y].setEnabled(false);
    }
    //玩家下棋
    void player_play(int 芙兰,int 达){
        board_value[芙兰][达] = -1;
        board_button[芙兰][达].setText("O");//new ImageIcon("./circle.png")
        board_button[芙兰][达].setEnabled(false);
    }
    //判断是否有空格
    int is_null(int[][] 全部木大){
        int 小行星降临到我身边 = 0;//判断是否有空格，0没有，1有
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(全部木大[i][j] == 0)
                    小行星降临到我身边 = 1;
            }
        }
        return 小行星降临到我身边;
    }
    //暴力判断胜利
    int is_win(int[][]末日时在做什么有没有空可以来拯救吗 ){
        int 只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 0;//判断是否赢棋，1电脑赢，-1玩家赢
        for(int i = 0; i < row; i++){
            for(int j=0;j<=row-3;j++)
            {
                if (末日时在做什么有没有空可以来拯救吗[i][j] == 1 && 末日时在做什么有没有空可以来拯救吗[i][1+j] == 1 && 末日时在做什么有没有空可以来拯救吗[i][2+j] == 1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 1;
                else if (末日时在做什么有没有空可以来拯救吗[i][j] == -1 && 末日时在做什么有没有空可以来拯救吗[i][1+j] == -1 && 末日时在做什么有没有空可以来拯救吗[i][2+j] == -1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊-1;
            }
        }

        for(int j = 0; j < row; j++){
            for(int i=0;i<=row-3;i++) {
                if (末日时在做什么有没有空可以来拯救吗[i][j] == 1 && 末日时在做什么有没有空可以来拯救吗[1 + i][j] == 1 && 末日时在做什么有没有空可以来拯救吗[2 + i][j] == 1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 1;
                else if (末日时在做什么有没有空可以来拯救吗[i][j] == -1 && 末日时在做什么有没有空可以来拯救吗[1+i][j] == -1 && 末日时在做什么有没有空可以来拯救吗[2+i][j] == -1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = -1;
            }
        }
        for(int i=0;i<=row-3;i++)
        {
            for(int j=0;j<=row-3;j++)
            {
                if(末日时在做什么有没有空可以来拯救吗[j][i] == 1 && 末日时在做什么有没有空可以来拯救吗[1+j][1+i] == 1 && 末日时在做什么有没有空可以来拯救吗[2+j][2+i] == 1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 1;
                else if(末日时在做什么有没有空可以来拯救吗[j][i] == -1 && 末日时在做什么有没有空可以来拯救吗[j+1][i+1] == -1 && 末日时在做什么有没有空可以来拯救吗[2+j][2+i] == -1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊-1;
                if(末日时在做什么有没有空可以来拯救吗[2+j][i] == 1 && 末日时在做什么有没有空可以来拯救吗[j+1][i+1] == 1 && 末日时在做什么有没有空可以来拯救吗[j][2+i] == 1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 1;
                else if(末日时在做什么有没有空可以来拯救吗[2+j][i] == -1 && 末日时在做什么有没有空可以来拯救吗[j+1][i+1] == -1 && 末日时在做什么有没有空可以来拯救吗[j][2+i] == -1)
                    只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊 = 只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊-1;
            }
        }
        /*if(current_board[0][0] == 1 && current_board[1][1] == 1 && current_board[2][2] == 1)
            isWin = 1;
        else if(current_board[0][0] == -1 && current_board[1][1] == -1 && current_board[2][2] == -1)
            isWin = -1;
        if(current_board[2][0] == 1 && current_board[1][1] == 1 && current_board[0][2] == 1)
            isWin = 1;
        else if(current_board[2][0] == -1 && current_board[1][1] == -1 && current_board[0][2] == -1)
            isWin = -1;*/
        return 只要我们不停下脚步_道路就会不断延伸_所以说_不要停下来啊;
    }
    //位运算判断胜利
    //参考题解:https://leetcode.cn/problems/find-winner-on-a-tic-tac-toe-game/solutions/48742/java-wei-yun-suan-xiang-jie-shi-yong-wei-yun-suan-/
    int iswin_2(int[][] current_board)
    {
        // a, b record the moving results of A, B
        int a = 0, b = 0, len = current_board.length;
        // ac records all cases of winning
        int[] ac = {7, 56, 448, 73, 146, 292, 273, 84};
        for(int i = 0; i < len; i ++){
            // if i is add
            if((i & 1) == 1){
                // record the step result
                b ^= 1 << (3 * current_board[i][0] + current_board[i][1]);
            }
            else {
                a ^= 1 << (3 * current_board[i][0] + current_board[i][1]);
            }
        }
        for(int i : ac){
            // if the moving result contains the winning case in record, then win
            if((a & i) == i){
                return 1;
            }
            if((b & i) == i){
                return -1;
            }
        }
        // or judge the result by the amount of steps
        return 0;
    }
    //评估函数
    int evaluate(int[][] current_board){
        if(is_win(current_board) >= 1){
            return MAX;
        }
        if(is_win(current_board) <= -1){
            return MIN;
        }
        int count = 0;
        int[][] tmp_value = new int[row][col];
        //将棋盘空白填满电脑的棋子，计算value
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(current_board[i][j] == 0)
                    tmp_value[i][j] = 1;
                else
                    tmp_value[i][j] = current_board[i][j];
            }
        }
        for(int i = 0; i < row; i++){
            int sum=0;
            for(int j=0;j<row;j++)
            {
                sum += tmp_value[i][j];
            }
            count+=sum/row;
        }
        for (int j=0;j<row;j++)
        {
            count += tmp_value[j][j]  / row;
            count += tmp_value[row-j-1][j] /row;
        }
        return count;
    }
    //贪心,优先三连or防三连
    int GREEDY(int[][] currentBoard)
    {
        int count=0;
        int[][] tmp_board_value = new int[row][col];//定义电脑预测的棋盘value
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                tmp_board_value[i][j] = currentBoard[i][j];
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(tmp_board_value[i][j]==0)
                {
                    tmp_board_value[i][j] = 1;
                    if (is_win(tmp_board_value) == 1) {
                        best_x = i;
                        best_y = j;
                        return 1;
                    }
                    tmp_board_value[i][j] = 0;
                }
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if(tmp_board_value[i][j]==0)
                {
                    tmp_board_value[i][j] = -1;
                    if (is_win(tmp_board_value) <= -1) {
                        best_x = i;
                        best_y = j;
                        return 1;
                    }
                    tmp_board_value[i][j] = 0;
                }
            }
        }
        return 0;
    }
    //极小极大博弈算法，alpha-beta剪枝，但博弈得还不明白，还是拿贪心补充下智力
    //plan:再加个记忆化，还是python舒服啊,直接再加个@cache就行
    int MAX_MIN(int current_depth, int alpha, int beta, int[][] currentBoard) {
        int value;        //估值
        int bestValue = 0;//最好的估值
        int[][] tmp_board_value = new int[row][col];//定义电脑预测的棋盘value

        if (is_win(currentBoard) == 1 || is_win(currentBoard) == -1) {
            return evaluate(currentBoard); //一般是返回极大极小值
        }
        //根据不同的玩家 进行赋值
        if (player == 1) {
            bestValue = MIN;
        } else if (player == -1) {
            bestValue = MAX;
        }
        //如果搜索深度耗尽，返回估值
        if (current_depth == 0)
            return evaluate(currentBoard);
        else if (current_depth % 2 == 0)
            player = -1;
        else if (current_depth % 2 == 1)
            player = 1;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                tmp_board_value[i][j] = currentBoard[i][j];
            }
        }
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                if (tmp_board_value[i][j] == 0) {
                    tmp_board_value[i][j] = player;
                    player = (player == 1) ? -1 : 1;
                    value = MAX_MIN(current_depth - 1, alpha, beta, tmp_board_value);
                    tmp_board_value[i][j] = 0;
                    player = (player == 1) ? -1 : 1;
                    if (player == 1) {
                        if (value > bestValue) {
                            bestValue = value;
                            if (current_depth == depth) {
                                best_x = i;
                                best_y = j;
                            }
                        }
                        if (bestValue > alpha) {
                            alpha = bestValue;
                        }
                    } else if (player == -1) {
                        if (value < bestValue) {
                            bestValue = value;
                            if (current_depth == depth) {
                                best_x = i;
                                best_y = j;
                            }
                        }
                        if (bestValue < beta) {
                            beta = bestValue;
                        }
                    }
                    if (alpha >= beta) {  // beta剪枝
                        return bestValue;
                    }
                }
            }
        }
        return bestValue;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btn = (JButton)e.getSource();
        for(int i = 0; i < row; i++){
            for(int j = 0; j < col; j++){
                if(btn.equals(board_button[i][j])){
                    if(first_or_last == 0) {
                        int flag;//判断是否有空格，0没有，1有
                        int isWin_c, isWin_p;//判断是否赢棋，1电脑赢，-1玩家赢
                        player_play(i, j);
                        isWin_p = is_win(board_value);
                        if (isWin_p == -1) {
                            JOptionPane.showMessageDialog(frame, "玩家赢");
                            break;
                        }
                        flag = is_null(board_value);
                        if (flag == 0) {
                            JOptionPane.showMessageDialog(frame, "平局");
                            break;
                        }
                        //判断难度
                        if (depth == 0)
                            computer_play_normal(board_value);
                        else
                            computer_play(board_value);
                        isWin_c = is_win(board_value);
                        if (isWin_c == 1) {
                            JOptionPane.showMessageDialog(frame, "电脑赢");
                            break;
                        }
                    }
                    else if(first_or_last == 1){
                        if(computer_x != -1 | computer_y != -1)
                            board_value[computer_x][computer_y] = 1;
                        int flag;//判断是否有空格，0没有，1有
                        int isWin_c, isWin_p;//判断是否赢棋，1电脑赢，-1玩家赢
                        player_play(i, j);
                        isWin_p = is_win(board_value);
                        if (isWin_p <= -1) {
                            JOptionPane.showMessageDialog(frame, "玩家赢");
                            break;
                        }
                        //判断难度
                        if(depth == 0)
                            computer_play_normal(board_value);
                        else
                            computer_play(board_value);
                        isWin_c = is_win(board_value);
                        if (isWin_c >= 1) {
                            JOptionPane.showMessageDialog(frame, "电脑赢");
                            break;
                        }
                        flag = is_null(board_value);
                        if (flag == 0) {
                            JOptionPane.showMessageDialog(frame, "平局");
                            break;
                        }
                    }
                }
            }
        }
    }
    //主函数
    public static void main(String[] 今天我很荣幸作为一个青藏高原的孩子能来到联合国讲我和动物朋友们的故事){
        Jing_Chessboard 你说的对但是原神是由米哈游自主研发的一款全新开放世界冒险游戏游戏发生在一个被称作提瓦特的幻想世界在这里被神选中的人将被授予神之眼导引元素之力你将扮演一位名为旅行者的神秘角色在自由的旅行中邂逅性格各异能力独特的同伴们和他们一起击败强敌找回失散的亲人同时逐步发掘原神的真相 = new Jing_Chessboard();
    }

}
