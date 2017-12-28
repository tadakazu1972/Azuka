package tadakazu1972.azuka;

import android.content.Context;

import com.opencsv.CSVParser;
import com.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Integer.parseInt;

/**
 * Created by tadakazu on 2017/08/16.
 */

public class Map {
    protected int index;
    protected int[][] data;
    protected int[] next; //時計回りに上右下左でつながったマップのindex保存用

    public Map(int _index){
        index = _index;
        data = new int[20][20];
        next = new int[4];
    }

    //CSVファイル読み込み
    protected void loadCSV(Context context, String filename){
        InputStream is = null;
        try {
            try {
                //assetsフォルダ内のcsvファイル読込
                is = context.getAssets().open(filename);
                InputStreamReader ir = new InputStreamReader(is, "UTF-8");
                CSVReader csvreader = new CSVReader(ir, CSVParser.DEFAULT_SEPARATOR, CSVParser.DEFAULT_QUOTE_CHARACTER, 0);//0行目から
                String[] csv;
                int y = 0;
                while((csv = csvreader.readNext()) != null){
                    for (int x=0;x<20;x++){
                        //読み込んだデータをある程度のレンジに変換して格納
                        int _data = parseInt(csv[x]);
                        data[y][x] = _data;
                    }
                    y++; if (y>20){ y=0;}
                }
                //データ代入
            } finally {
                if (is != null) is.close();
                //Toast.makeText(this, "マップデータ読込完了", Toast.LENGTH_SHORT).show();
            }
        } catch(Exception e){
            //Toast.makeText(this, "CSV読込エラー", Toast.LENGTH_SHORT).show();
        }
    }

    //next
    protected void setNext(int up, int right, int down, int left){
        next[0] = up;
        next[1] = right;
        next[2] = down;
        next[3] = left;
    }
}
