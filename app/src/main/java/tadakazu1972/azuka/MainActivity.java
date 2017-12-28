package tadakazu1972.azuka;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

public class MainActivity extends AppCompatActivity implements android.view.View.OnTouchListener {

    protected SurfaceView surfaceview; //activity_main.xmlの中のSurfaceView領域保持用
    protected MainSurfaceView mainSurfaceView; //クラス保持用
    //解像度拡大用
    protected int deviceWidth;
    protected int deviceHeight;
    //メンバクラス
    protected tadakazu1972.azuka.MyChara mMyChara;
    protected Map[] mMap;
    protected int MX = 19; //マップX最大値-1
    protected int MY = 19; //マップY最大値-1
    protected DrawMap mDrawMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //メンバ変数初期化
        initMyChara();
        initMap();

        //xmlをセット。こちらを先に設定しないと後でSurfaceViewを捕獲できずNull Pointer Exceptionになる
        setContentView(R.layout.activity_main);

        //端末の画面サイズ取得
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        deviceWidth = size.x;
        deviceHeight = size.y;

        //SurfaceView生成
        surfaceview = (SurfaceView)findViewById(R.id.MainSurfaceView);
        mainSurfaceView = new tadakazu1972.azuka.MainSurfaceView(this, surfaceview, deviceWidth, deviceHeight);

        //ボタン初期化
        initButtons();

    }

    private void initMyChara(){
        //初期化
        mMyChara = new tadakazu1972.azuka.MyChara(this);
    }


    private void initMap(){
        //初期化
        mMap = new Map[7];
        //csv読み込み
        for(int i=0;i<7;i++){
            mMap[i] = new Map(i);
            //csvファイルからマップデータを読み込み
            String filename = String.format("level%d.csv", i);
            mMap[i].loadCSV(this, filename);
        }
        //next
        mMap[0].setNext(0,0,1,0);
        mMap[1].setNext(0,2,0,0);
        mMap[2].setNext(0,3,0,1);
        mMap[3].setNext(0,4,0,2);
        mMap[4].setNext(0,0,5,3);
        mMap[5].setNext(4,0,6,0);
        mMap[6].setNext(5,0,0,0);

        //描画用マップ
        mDrawMap = new DrawMap();
        //基底マップのデータを描画用マップに転送
        transMap();
    }

    protected void transMap(){
        //1で埋める
        for(int i=0;i<5;i++){
            for(int j=0;j<5;j++){
                mDrawMap.data[i][j]=1;
            }
        }
        //mMyChara.dirの値に応じて元マップから表示用配列に転送
        switch (mMyChara.dir){
            case 0:
                for(int x=4;x>-1;x--){
                    for (int y=-2;y<3;y++){
                        if ((mMyChara.mx + x <= MY) && (mMyChara.my + y >= 0) && (mMyChara.my + y <= MX)) {
                            mDrawMap.data[4 - x][y + 2] = mMap[mMyChara.currentMap].data[mMyChara.my + y][mMyChara.mx + x];
                        }
                    }
                }
                break;
            case 1:
                for(int y=-4;y<1;y++){
                    for(int x=-2;x<3;x++){
                        if ((mMyChara.my + y >= 0) && (mMyChara.mx + x >= 0) && (mMyChara.mx + x <= MY)) {
                            mDrawMap.data[y + 4][x + 2] = mMap[mMyChara.currentMap].data[mMyChara.my + y][mMyChara.mx + x];
                        }
                    }
                }
                break;
            case 2:
                for(int x=-4;x<1;x++){
                    for(int y=2;y>-3;y--){
                        if ((mMyChara.mx + x >= 0) && (mMyChara.my + y >= 0) && (mMyChara.my + y <= MY)) {
                            mDrawMap.data[4 + x][2 - y] = mMap[mMyChara.currentMap].data[mMyChara.my + y][mMyChara.mx + x];
                        }
                    }
                }
                break;
            case 3:
                for(int y=4;y>-1;y--){
                    for(int x=2;x>-3;x--){
                        if ((mMyChara.my + y <= MY) && (mMyChara.mx + x >= 0) && (mMyChara.mx + x <= MY)) {
                            mDrawMap.data[4 - y][2 - x] = mMap[mMyChara.currentMap].data[mMyChara.my + y][mMyChara.mx + x];
                        }
                    }
                }
                break;
        }
    }

    private void initButtons(){
        //ボタンにonTouchイベント設定
        findViewById(R.id.btnUp).setOnTouchListener(this);
        findViewById(R.id.btnRight).setOnTouchListener(this);
        findViewById(R.id.btnDown).setOnTouchListener(this);
        findViewById(R.id.btnLeft).setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event){
        //onTouchは画面を押した時と離した時の両方のイベントを取得する
        int action = event.getAction();
        switch(action){
            //ボタンから指が離れた時
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_DOWN:
                switch(v.getId()){
                    case R.id.btnUp:
                        //前進
                        mMyChara.checkMove();
                        transMap();
                        break;
                    case R.id.btnRight:
                        //右回り
                        mMyChara.dir = (mMyChara.dir + 3) & 3;
                        transMap();
                        break;
                    case R.id.btnDown:
                        //振り向き
                        mMyChara.dir = (mMyChara.dir + 2) & 3;
                        transMap();
                        break;
                    case R.id.btnLeft:
                        //左周り
                        mMyChara.dir = (mMyChara.dir + 1) & 3;
                        transMap();
                        break;
                }
        }
        return false;
    }
}
