package tadakazu1972.azuka;

/**
 * Created by tadakazu on 2017/08/26.
 */

public class MyChara {
    public MainActivity ac;
    public float x, y, vx, vy;
    public float l, r, t, b;
    public int base_index; //アニメーション用 0:上 1:右 2:下 3:左
    public int index; //アニメーション用カウンタ
    public int currentMap; //現在キャラが存在するマップID
    protected int mx, my;
    protected int dir;

    public MyChara(MainActivity _ac){
        ac = _ac;
        x = 4*32.0f;
        y = 7*32.0f;
        vx = 0.0f;
        vy = 0.0f;
        l = x;
        r = x+31.0f;
        t = y;
        b = y+31.0f;
        base_index = 4;
        index = 0;
        currentMap = 0;
        mx = 4;
        my = 4;
        dir = 1;
    }

    public void move(){
        //当たり判定用マップ座標算出
        int x1=(int)(x+8.0f+vx)/32;  if (x1<0) x1=0; if (x1>9) x1=9;
        int y1=(int)(y+8.0f+vy)/32;  if (y1<0) y1=0; if (y1>9) y1=9;
        int x2=(int)(x+24.0f+vx)/32; if (x2>9) x2=9; if (x2<0) x2=0;
        int y2=(int)(y+24.0f+vy)/32; if (y2>9) y2=9; if (y2<0) y2=0;
        //カベ判定
        if (ac.mMap[currentMap].data[y1][x1] > 0 || ac.mMap[currentMap].data[y1][x2] > 0 || ac.mMap[currentMap].data[y2][x1] > 0 || ac.mMap[currentMap].data[y2][x2] > 0) {
            vx = 0.0f;
            vy = 0.0f;
        }
        //画面端判定
        if (x < -8.0f) {
            currentMap = ac.mMap[currentMap].next[3];
            x = 288.0f;
            ac.transMap();
        }
        if (x > 296.0f) { //画面右端1/4キャラ(8px)以上
            currentMap = ac.mMap[currentMap].next[1];
            x = 0.0f;
            ac.transMap();
        }
        if (y < -8.0f) {
            currentMap = ac.mMap[currentMap].next[0];
            y = 288.0f;
            ac.transMap();
        }
        if (y > 296.0f) {  //画面下1/4キャラ(8px)以上
            currentMap = ac.mMap[currentMap].next[2];
            y = 0.0f;
            ac.transMap();
        }
        //座標更新
        x = x + vx;
        y = y + vy;
        //アニメーションインデックス変更処理
        index++;
        if (index > 19) index = 0;
    }

    public void up(int i){
        if (i<2) i=1;
        vx =  0.0f;
        vy = -1.0f * i;
        base_index= 0;
    }

    public void right(int i){
        if (i<2) i=1;
        vx =  1.0f * i;
        vy =  0.0f;
        base_index= 2;
    }

    public void down(int i){
        if (i<2) i=1;
        vx =  0.0f;
        vy =  1.0f * i;
        base_index= 4;
    }

    public void left(int i){
        if (i<2) i=1;
        vx = -1.0f * i;
        vy =  0.0f;
        base_index= 6;
    }
}
