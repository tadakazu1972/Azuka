package tadakazu1972.azuka;

/**
 * Created by tadakazu on 2017/12/24.
 */

public class DrawMap {
    protected int[][] data;

    public DrawMap(){
        data = new int[5][5];
        //1で埋める
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                data[i][j]=1;
            }
        }
    }
}
