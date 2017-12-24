package tadakazu1972.azuka;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.nio.FloatBuffer;

/**
 * Created by tadakazu on 2017/08/16.
 */

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private MainActivity ac;
    private SurfaceHolder holder;
    private Thread thread;
    private float scaleX;
    private float scaleY;
    private float scale;
    private boolean isSurfaceStandby = false;
    //画像 sはspriteのs
    private Bitmap[] sMyChara;
    private Bitmap[] sMap;
    //ワイヤーフレーム描画用
    private Paint mPaint = new Paint();

    public MainSurfaceView(Context context, SurfaceView _surfaceview, int _deviceWidth, int _deviceHeight){
        super(context);

        //MainActivity保存。あとでマップCSV読み込みの時にassetsフォルダをアクセスする時に必要
        ac = (MainActivity)context;

        //画像読み込み
        initBitmap();

        //SurfaceHolder取得
        holder = _surfaceview.getHolder(); //MainActivityで確保したのを受け取る必要あり
        holder.addCallback(this);
        holder.setFixedSize(getWidth(), getHeight());

        //端末の画面サイズをもとに画像拡大倍率計算
        scaleX = _deviceWidth / 320.0f; //32pxx32pxの画像を10x10並べているので
        scaleY = _deviceHeight / 320.0f;
        scale = scaleX > scaleY ? scaleY : scaleX;

        //ゲームの各種ステータスをロード
        loadData();

        //フォーカスをあてる
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        //毎回この後にsurfaceChangedが呼ばれるからそちらで記述している
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        thread = null;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
        //準備完了、描画スレッド起動
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run(){
        while (thread != null){

            //描画
            draw(holder);

            //移動処理
            ac.mMyChara.move();

        }
    }

    public void draw(SurfaceHolder holder){
        //ホルダーからキャンパス取得
        Canvas canvas = holder.lockCanvas();
        if (canvas !=null){
            //端末の画面サイズにあわせて拡大
            canvas.scale(scale, scale);
            //現在の状態を保存
            canvas.save();

            //背景を塗る
            canvas.drawColor(Color.BLACK);

            //画像描画
            drawMap(canvas);
            drawMyChara(canvas);

            //ワイヤーフレーム描画
            //drawLines(canvas);
            drawPath(canvas);

            //現在の状態の変更
            canvas.restore();

            //描画内容確定
            holder.unlockCanvasAndPost(canvas);
        }

    }

    private void drawMyChara(Canvas canvas){
        int i = ac.mMyChara.base_index + ac.mMyChara.index / 10;
        if ( i > 7) i = 0;
        canvas.drawBitmap(sMyChara[i], ac.mMyChara.x, ac.mMyChara.y, null);
    }

    private void drawMap(Canvas canvas){
        int mapIndex = ac.mMyChara.currentMap;
        int mapId = 0;
        for (int y=0; y<10; y++){
            for (int x=0; x<10; x++){
                mapId = ac.mMap[mapIndex].data[y][x];
                canvas.drawBitmap(sMap[mapId], x*32.0f, y*32.0f, null);
            }
        }
    }


    private void drawPath(Canvas canvas){
        //変数初期化
        Path path = new Path();
        FloatBuffer fb = FloatBuffer.allocate(400);

        //座標データ用意
        float pt[][] = {
                {  0,120,   0,180,  60,180,  60,120},
                { 60,120,  60,180, 120,180, 120,120},
                {120,120, 120,180, 180,180, 180,120},
                {180,120, 180,180, 240,180, 240,120},
                {240,120, 240,180, 300,180, 300,120},
                {  0,100,   0,200,  60,180,  60,120},
                {100,100, 100,200, 120,180, 120,120},
                {200,100, 200,200, 180,180, 180,120},
                {300,100, 300,200, 240,180, 240,120},
                {  0,100,   0,200, 100,200, 100,100},
                {100,100, 100,200, 200,200, 200,100},
                {200,100, 200,200, 300,200, 300,100},
                { 70, 70,  70,230, 100,200, 100,100},
                {230, 70, 230,230, 200,200, 200,100},
                {  0, 70,   0,230,  70,230,  70, 70},
                { 70, 70,  70,230, 230,230, 230, 70},
                {230, 70, 230,230, 300,230, 300, 70},
                { 30, 30,  30,270,  70,230,  70, 70},
                {230, 70, 230,230, 270,270, 270, 30},
                {  0, 30,   0,270,  30,270,  30, 30},
                { 30, 30,  30,270, 270,270, 270, 30},
                {270, 30, 270,270, 300,270, 300, 30},
                {  0,  0,   0,300,  30,270,  30, 30},
                {270, 30, 270,270, 300,300, 300,  0}
        };

        //座標データをポリゴン描画用にパスとして格納、またLine描画用に配列に格納
        for(int i=0;i<24;i++){
            //壁
            path.moveTo(pt[i][0],pt[i][1]);
            path.lineTo(pt[i][2],pt[i][3]);
            path.lineTo(pt[i][4],pt[i][5]);
            path.lineTo(pt[i][6],pt[i][7]);
            //壁の枠のデータを配列に格納(drawLines用に設定)
            float data[] = {pt[i][0],pt[i][1], pt[i][2],pt[i][3], pt[i][2],pt[i][3], pt[i][4],pt[i][5], pt[i][4],pt[i][5], pt[i][6],pt[i][7], pt[i][6],pt[i][7], pt[i][0],pt[i][1]};
            fb.put(data);
        }
        path.close();

        //壁を黒で塗りつぶす
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, mPaint);

        //壁の枠を白で描画
        mPaint.setColor(Color.WHITE);
        canvas.drawLines(fb.array(), mPaint);
    }


    private void drawLines(Canvas canvas){
        mPaint.setStrokeWidth(1.0f);
        mPaint.setColor(Color.WHITE);

        FloatBuffer pt = FloatBuffer.allocate(400);

        // ４ブロック先　正面
        //(0,0)の正面の壁
        float data1[] = {  0,120,   0,180,   0,180,  60,180,  60,180,  60,120,  60,120,  0,120};
        for (float data : data1) {
            pt.put(data);
        }
        //(0,1)の正面の壁
        float data2[] = { 60,120,  60,180,  60,180, 120,180, 120,180, 120,120, 120,120,  60,120};
        for (float data : data2) {
            pt.put(data);
        }
        //(0,2)の正面の壁
        float data3[] = {120,120, 120,180, 120,180, 180,180, 180,180, 180,120, 180,120, 120,120};
        for (float data : data3) {
            pt.put(data);
        }
        //(0,3)の正面の壁
        float data4[] = {180,120, 180,180, 180,180, 240,180, 240,180, 240,120, 240,120, 180,120};
        for (float data : data4) {
            pt.put(data);
        }
        //(0,4)の正面の壁
        float data5[] = {240,120, 240,180, 240,180, 300,180, 300,180, 300,120, 300,120, 240,120};
        for (float data : data5) {
            pt.put(data);
        }

        // ３ブロック先　横
        //(1,0)の横の壁
        float data6[] = {  0,100,   0,200,   0,200,  60,180,  60,180,  60,120,  60,120,   0,100};
        for (float data : data6) {
            pt.put(data);
        }
        //(1,1)の横の壁
        float data7[] = {100,100, 100,200, 100,200, 120,180, 120,180, 120,120, 120,120, 100,100};
        for (float data : data7) {
            pt.put(data);
        }
        //(1,3)の横の壁
        float data8[] = {200,100, 200,200, 200,200, 180,180, 180,180, 180,120, 180,120, 200,100};
        for (float data : data8) {
            pt.put(data);
        }
        //(1,4)の横の壁
        float data9[] = {300,100, 300,200, 300,200, 240,180, 240,180, 240,120, 240,120, 300,100};
        for (float data : data9) {
            pt.put(data);
        }

        // ３ブロック先　正面
        //(1,1)の正面の壁
        float data10[] = {  0,100,   0,200,   0,200, 100,200, 100,200, 100,100, 100,100,   0,100};
        for (float data : data10) {
            pt.put(data);
        }
        //(1,2)の正面の壁
        float data11[] = {100,100, 100,200, 100,200, 200,200, 200,200, 200,100, 200,100, 100,100};
        for (float data : data11) {
            pt.put(data);
        }
        //(1,3)の正面の壁
        float data12[] = {200,100, 200,200, 200,200, 300,200, 300,200, 300,100, 300,100, 200,100};
        for (float data : data12) {
            pt.put(data);
        }

        // ２ブロック先　横
        //(2,1)の正面の壁
        float data13[] = { 70, 70,  70,230,  70,230, 100,200, 100,200, 100,100, 100,100,  70, 70};
        for (float data : data13) {
            pt.put(data);
        }
        //(2,3)の正面の壁
        float data14[] = {230, 70, 230,230, 230,230, 200,200, 200,200, 200,100, 200,100, 230, 70};
        for (float data : data14) {
            pt.put(data);
        }

        // ２ブロック先　正面
        //(2,1)の正面の壁
        float data15[] = {  0, 70,   0,230,   0,230,  70,230,  70,230,  70, 70,  70, 70,   0, 70};
        for (float data : data15) {
            pt.put(data);
        }
        //(2,2)の正面の壁
        float data16[] = { 70, 70,  70,230,  70,230, 230,230, 230,230, 230, 70, 230, 70,  70, 70};
        for (float data : data16) {
            pt.put(data);
        }
        //(2,3)の正面の壁
        float data17[] = {230, 70, 230,230, 230,230, 300,230, 300,230, 300, 70, 300, 70, 230, 70};
        for (float data : data17) {
            pt.put(data);
        }

        // １ブロック先　横
        //(3,1)の横の壁
        float data18[] = { 30, 30,  30,270,  30,270,  70,230,  70,230,  70, 70,  70, 70,  30, 30};
        for (float data : data18) {
            pt.put(data);
        }
        //(3,3)の横の壁
        float data19[] = {270, 30, 270,270, 270,270, 230,230, 230,230, 230, 70, 230, 70, 270, 30};
        for (float data : data19) {
            pt.put(data);
        }

        // １ブロック先　正面
        //(3,1)の正面の壁
        float data20[] = {  0, 30,   0,270,   0,270,  30,270,  30,270,  30, 30,  30, 30,   0, 30};
        for (float data : data20) {
            pt.put(data);
        }
        //(3,2)の正面の壁
        float data21[] = { 30, 30,  30,270,  30,270, 270,270,  270,270, 270, 30, 270, 30, 30, 30};
        for (float data : data21) {
            pt.put(data);
        }
        //(3,3)の正面の壁
        float data22[] = {270, 30, 270,270, 270,270, 300,270, 300,270, 300, 30, 300, 30, 270, 30};
        for (float data : data22) {
            pt.put(data);
        }

        // 自分がいる場所の左右の壁
        //(4,1)の横の壁
        float data23[] = {  0,  0,   0,300,   0,300,  30,270,  30,270,  30, 30,  30, 30,   0,  0};
        for (float data : data23) {
            pt.put(data);
        }
        //(4,3)の横の壁
        float data24[] = {300,  0, 300,300, 300,300, 270,270, 270,270, 270, 30, 270, 30, 300,  0};
        for (float data : data24) {
            pt.put(data);
        }

        // 最後の囲み四角
        float data25[] = {  0,  0,   0,300,   0,300, 300,300, 300,300,   0,300,   0,300,   0,  0};
        for (float data : data25) {
            pt.put(data);
        }

        //配列に格納した全ての座標を使って描画
        canvas.drawLines(pt.array(), mPaint);

    }

    //画像初期化
    private void initBitmap(){
        Resources res = getResources();
        //自キャラ
        sMyChara = new Bitmap[8];
        sMyChara[0] = BitmapFactory.decodeResource(res, R.drawable.arthur07);
        sMyChara[1] = BitmapFactory.decodeResource(res, R.drawable.arthur08);
        sMyChara[2] = BitmapFactory.decodeResource(res, R.drawable.arthur03);
        sMyChara[3] = BitmapFactory.decodeResource(res, R.drawable.arthur04);
        sMyChara[4] = BitmapFactory.decodeResource(res, R.drawable.arthur01);
        sMyChara[5] = BitmapFactory.decodeResource(res, R.drawable.arthur02);
        sMyChara[6] = BitmapFactory.decodeResource(res, R.drawable.arthur05);
        sMyChara[7] = BitmapFactory.decodeResource(res, R.drawable.arthur06);
        //Map
        sMap = new Bitmap[2];
        sMap[0] = BitmapFactory.decodeResource(res, R.drawable.greenfield);
        sMap[1] = BitmapFactory.decodeResource(res, R.drawable.tree);
    }

    //ゲームの各種ステータスをロード
    private void loadData(){
    }

}
