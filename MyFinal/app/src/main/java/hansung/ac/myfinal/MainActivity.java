package hansung.ac.myfinal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<dataSample> dataSamples =new ArrayList<>();
    private String selected = new String();
    int searchlength;
    int flag=0;//top 20 정렬방법
    int searchType=3; //0은 영화제목 검색, 1은 배우명으로 검색

    myDBHelper myHelper = new myDBHelper(this);
    ListView simpleList;
    String[] dates = new String[100];
    int [] runtimes = new int[100];
    String[] movieList20count =new String[20];
    String[] movieList20avg =new String[20];
    String[] imdb=new String[100];
    String[] movieList = new String[100];
    String[] movieListavg =new String[100];
    Double[][] vote_avg = new Double[100][2];
    Double[] ratings = new Double[100];
    String[][] genreList= new String[100][5];
    String [] overviews= new String[100];
    String [] taglines=new String[100];
    String [] actors=new String[100];
    String [] actors_avg = new String[100];
    String [] searchList2;
    Integer poster = R.drawable.poster_sample;

    //영화 장르별 리스트
    List<String>  Action = new ArrayList<>();

    String[] Action20 = new String[20];
    List<String> Adventure = new ArrayList<>();
    List<String> Drama = new ArrayList<>();
    List<String> Mystery = new ArrayList<>();
    List<String> Comedy = new ArrayList<>();
    List<String> ScienceFiction = new ArrayList<>();
    List<String> Thriller = new ArrayList<>();
    String[] Thriller20 = new String[20];
    List<String> Crime = new ArrayList<>();
    List<String> Fantasy = new ArrayList<>();
    List<String> Family = new ArrayList<>();
    List<String> Animation = new ArrayList<>();
    List<String> War = new ArrayList<>();
    List<String> Romance = new ArrayList<>();

    Button btn1, btn2, search, reviewBtn;
    SQLiteDatabase sqlDB;
    EditText edt, reviewEdt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("OTT 소개 어플리케이션");

        readData(getResources().openRawResource(R.raw.movies_metadata100),1);//1은 전체 data
        readData(getResources().openRawResource(R.raw.genres),2);//2는 장르
        readData(getResources().openRawResource(R.raw.overview),3);//3은 줄거리
        readData(getResources().openRawResource(R.raw.tagline),4);//4는 한줄설명
        readData(getResources().openRawResource(R.raw.actors),5);//5는 배우명

        TabHost tabhost = findViewById(R.id.tabHost);
        tabhost.setup();
        TabHost.TabSpec spec = tabhost.newTabSpec("main Tab");
        spec.setContent(R.id.mainTab);
        spec.setIndicator("메인");
        tabhost.addTab(spec);

        spec = tabhost.newTabSpec("search Tab");
        spec.setContent(R.id.searchTab);
        spec.setIndicator("검색");
        tabhost.addTab(spec);

        spec = tabhost.newTabSpec("search Tab");
        spec.setContent(R.id.detailTab);
        spec.setIndicator("상세보기");
        tabhost.addTab(spec);

        for(int i=0;i<20;i++){
            movieList20count[i]=movieList[i];
            Action20[i]=Action.get(i);
            Thriller20[i]=Thriller.get(i);

        }
        String[] Romance20 = new String[Romance.size()];
        for(int i=0;i<Romance.size();i++){
            Romance20[i]=Romance.get(i);
        }
        simpleList = (ListView) findViewById(R.id.listView1);
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), movieList20count, poster);
        simpleList.setAdapter(customAdapter);

        simpleList = (ListView) findViewById(R.id.listView2);//액션
        customAdapter = new CustomAdapter(getApplicationContext(),Action20, poster);
        simpleList.setAdapter(customAdapter);

        simpleList = (ListView) findViewById(R.id.listView3);//스릴러
        customAdapter = new CustomAdapter(getApplicationContext(),Thriller20, poster);
        simpleList.setAdapter(customAdapter);

        simpleList = (ListView) findViewById(R.id.listView4);//로맨스
        customAdapter = new CustomAdapter(getApplicationContext(),Romance20, poster);
        simpleList.setAdapter(customAdapter);

        btn1=(Button) findViewById(R.id.button1);
        btn2=(Button) findViewById(R.id.button2);
        reviewBtn=(Button) findViewById(R.id.reviewBtn);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //평점 개수로 정렬 (og)
                flag=0;
                simpleList = (ListView) findViewById(R.id.listView1);
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), movieList20count, poster);
                simpleList.setAdapter(customAdapter);
            }
        });
        for (int i=0;i<100;i++){
            vote_avg[i][0]=(double)i;
        }
        Arrays.sort(vote_avg, (a, b) -> Double.compare(b[1],a[1])); //평점 평균으로 정렬한 영화 리스트

        for(int i=0;i<100;i++){
            int index=vote_avg[i][0].intValue();
            movieListavg[i]=movieList[index];
            actors_avg[i]=actors[index];
        }
        for(int i=0;i<20;i++){
            movieList20avg[i]=movieListavg[i];
        }
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //평점 평균으로 정렬
                simpleList = (ListView) findViewById(R.id.listView1);
                flag=1;
                CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), movieList20avg, poster);
                simpleList.setAdapter(customAdapter);
            }
        });
        search=(Button) findViewById(R.id.searchBtn);
        simpleList = (ListView) findViewById(R.id.searchLists);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchType=3;
                List<String>searchList1 = new ArrayList<>();
                edt = (EditText) findViewById(R.id.searchText);
                selected = edt.getText().toString();
                int n=0, i=0;
                while(n<100&&i<5){
                    if(movieListavg[n].contains(selected)){
                        searchList1.add(movieListavg[n]);
                        i++;
                        searchType=0;
                    }
                    n++;
                }
                if(searchType==3){
                    i=0;
                    n=0;
                    while(n<100&&i<5) {
                        if (actors_avg[n].contains(selected)) {
                            searchList1.add(movieListavg[n]);
                            i++;
                        }
                        n++;
                    }
                }

                TextView nothing = (TextView) findViewById(R.id.no_result);
                if(searchList1.size()==0){
                    nothing.setText("검색결과가 없습니다.");
                    simpleList.setVisibility(View.INVISIBLE);
                    nothing.setVisibility(View.VISIBLE);
                }
                else{
                    searchlength=searchList1.size();
                    searchList2=new String[searchlength];
                    nothing.setVisibility(View.INVISIBLE);
                    simpleList.setVisibility(View.VISIBLE);
                    for(int k=0;k< searchList1.size();k++)
                        searchList2[k]= searchList1.get(k);
                    CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), searchList2, poster);
                    simpleList.setAdapter(customAdapter);
                }

            }
        });
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        reviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reviewEdt = (EditText)findViewById(R.id.reviewEdt);

                sqlDB = myHelper.getWritableDatabase();
                sqlDB.execSQL("INSERT INTO groupTBL VALUES ( '"
                        +String.valueOf(ratingBar.getRating())+"',"
                        +reviewEdt.getText().toString()+");");
                Toast.makeText(getApplicationContext(),"입력됨",Toast.LENGTH_SHORT).show();

                Cursor cursor;
                sqlDB = myHelper.getReadableDatabase();
                cursor = sqlDB.rawQuery("SELECT * FROM groupTBL;",null);
                String ratingValue=String.valueOf(ratingBar.getRating());
                String strRating = "rating" + "\r\n" + "----------"+"\r\n";
                String strReview =  "review" + "\r\n" + "----------"+"\r\n";

                while (cursor.moveToNext()){
                    strRating+=cursor.getString(0)+"\r\n";
                    strReview+=cursor.getString(1)+"\r\n";
                }
                cursor.close();
                sqlDB.close();
                //edtNameResult.setText(strNames);
                //edtNumberResult.setText(strNumbers);
            }
        });

        ListView top20 =(ListView) findViewById(R.id.listView1);
        ListView actTop20 = (ListView) findViewById(R.id.listView2);
        ListView thrillerTop20 = (ListView) findViewById(R.id.listView3);
        ListView romanceTop20 = (ListView) findViewById(R.id.listView4);
        ListView searched = (ListView) findViewById(R.id.searchLists);

        TextView title = (TextView) findViewById(R.id.title1);
        TextView overView = (TextView) findViewById(R.id.overview1);
        TextView tagline = (TextView) findViewById(R.id.tagline1);
        TextView actor = (TextView) findViewById(R.id.actor);
        TextView genre = (TextView)findViewById(R.id.genre1);
        TextView rating = (TextView)findViewById(R.id.rating);
        TextView date = (TextView)findViewById(R.id.date);
        TextView runtime = (TextView)findViewById(R.id.runtime);


        title.setText(movieList[0]); // 첫번째걸로 초기화
        tagline.setText(taglines[0]);
        actor.setText(actors[0]);
        overView.setText(overviews[0]);
        genre.setText("genre: "+genreList[0][0]+"   "+genreList[0][1]+"   "+genreList[0][2]+"   "+genreList[0][3]+"   "+genreList[0][4]);
        rating.setText("★ "+ratings[0].toString());
        date.setText("|  "+dates[0]);
        runtime.setText("|  "+runtimes[0]+"min");
//        Intent links = new Intent(Intent.ACTION_VIEW, Uri.parse());
//        startActivity(links);
//        WebView wb = (WebView) findViewById(R.id.webView);
//        wb.loadUrl("https://www.imdb.com/title/"+imdb[0]+"/reviews/?ref_=tt_ql_2");

        top20.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),detailPage.class);

                if(flag==0){
                intent.putExtra("title",movieList[i]);
                intent.putExtra("tagline",taglines[i]);
                intent.putExtra("actor",actors[i]);
                intent.putExtra("overview",overviews[i]);
                title.setText(movieList[i]);
                tagline.setText(taglines[i]);
                overView.setText(overviews[i]);
                actor.setText(actors[i]);

                for(int j=1;j<5;j++){
                    if(genreList[i][j]==null)
                        genreList[i][j]="";
                    else genreList[i][j]=", "+genreList[i][j];
                }
                intent.putExtra("genre1",genreList[i][0]);
                intent.putExtra("genre2",genreList[i][1]);
                intent.putExtra("genre3",genreList[i][2]);
                intent.putExtra("genre4",genreList[i][3]);
                intent.putExtra("genre5",genreList[i][4]);

                genre.setText("genre: "+genreList[i][0]+genreList[i][1]+genreList[i][2]+genreList[i][3]+genreList[i][4]);

                intent.putExtra("rating",ratings[i]);
                intent.putExtra("date",dates[i]);
                intent.putExtra("runtime",runtimes[i]);

                startActivityForResult(intent,0);}
                else{
                    intent.putExtra("title",movieList20avg[i]);
                    int k=0;
                    for(k=0;k<100;k++){
                        if(movieList[k]==movieList20avg[i])break;
                    }
                    intent.putExtra("tagline",taglines[k]);
                    intent.putExtra("overview",overviews[k]);

                    title.setText(movieList20avg[i]);
                    tagline.setText(taglines[k]);
                    overView.setText(overviews[k]);
                    actor.setText(actors[k]);

                    for(int j=1;j<5;j++){
                        if(genreList[k][j]==null)
                            genreList[k][j]="";
                        else genreList[k][j]=", "+genreList[k][j];
                    }

                    intent.putExtra("genre1",genreList[k][0]);
                    intent.putExtra("genre2",genreList[k][1]);
                    intent.putExtra("genre3",genreList[k][2]);
                    intent.putExtra("genre4",genreList[k][3]);
                    intent.putExtra("genre5",genreList[k][4]);
                    genre.setText("genre: "+genreList[k][0]+"   "+genreList[k][1]+"   "+genreList[k][2]+"   "+genreList[k][3]+"   "+genreList[k][4]);
                    intent.putExtra("rating",ratings[k]);
                    intent.putExtra("date",dates[k]);
                    intent.putExtra("runtime",runtimes[k]);
                    intent.putExtra("actor",actors[k]);
                    startActivityForResult(intent,0);
                }

            }
        });
        actTop20.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),detailPage.class);
                intent.putExtra("title",Action20[i]);
                int k=0;
                for(k=0;k<100;k++){
                    if(movieList[k]==Action20[i])break;
                }
                intent.putExtra("tagline",taglines[k]);
                intent.putExtra("overview",overviews[k]);

                title.setText(Action20[i]);
                tagline.setText(taglines[k]);
                overView.setText(overviews[k]);
                actor.setText(actors[k]);

                for(int j=1;j<5;j++){
                    if(genreList[k][j]==null)
                        genreList[k][j]="";
                    else genreList[k][j]=", "+genreList[k][j];
                }

                intent.putExtra("genre1",genreList[k][0]);
                intent.putExtra("genre2",genreList[k][1]);
                intent.putExtra("genre3",genreList[k][2]);
                intent.putExtra("genre4",genreList[k][3]);
                intent.putExtra("genre5",genreList[k][4]);
                genre.setText("genre: "+genreList[k][0]+"   "+genreList[k][1]+"   "+genreList[k][2]+"   "+genreList[k][3]+"   "+genreList[k][4]);

                intent.putExtra("rating",ratings[k]);
                intent.putExtra("date",dates[k]);
                intent.putExtra("runtime",runtimes[k]);
                intent.putExtra("actor",actors[k]);

                startActivityForResult(intent,0);
            }
        });
        thrillerTop20.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),detailPage.class);
                intent.putExtra("title",Thriller20[i]);
                int k=0;
                for(k=0;k<100;k++){
                    if(movieList[k]==Thriller20[i])break;
                }
                intent.putExtra("tagline",taglines[k]);
                intent.putExtra("overview",overviews[k]);

                title.setText(Thriller20[i]);
                tagline.setText(taglines[k]);
                overView.setText(overviews[k]);
                actor.setText(actors[k]);

                for(int j=1;j<5;j++){
                    if(genreList[k][j]==null)
                        genreList[k][j]="";
                    else genreList[k][j]=", "+genreList[k][j];
                }
                intent.putExtra("genre1",genreList[k][0]);
                intent.putExtra("genre2",genreList[k][1]);
                intent.putExtra("genre3",genreList[k][2]);
                intent.putExtra("genre4",genreList[k][3]);
                intent.putExtra("genre5",genreList[k][4]);
                genre.setText("genre: "+genreList[k][0]+genreList[k][1]+genreList[k][2]+genreList[k][3]+genreList[k][4]);

                intent.putExtra("rating",ratings[k]);
                intent.putExtra("date",dates[k]);
                intent.putExtra("runtime",runtimes[k]);
                intent.putExtra("actor",actors[k]);

                startActivityForResult(intent,0);
            }
        });
        romanceTop20.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),detailPage.class);
                intent.putExtra("title",Romance20[i]);
                int k=0;
                for(k=0;k<100;k++){
                    if(movieList[k]==Romance20[i])break;
                }
                intent.putExtra("tagline",taglines[k]);
                intent.putExtra("overview",overviews[k]);

                title.setText(Romance20[i]);
                tagline.setText(taglines[k]);
                overView.setText(overviews[k]);
                actor.setText(actors[k]);

                for(int j=1;j<5;j++){
                    if(genreList[k][j]==null)
                        genreList[k][j]="";
                    else genreList[k][j]=", "+genreList[k][j];
                }

                intent.putExtra("genre1",genreList[k][0]);
                intent.putExtra("genre2",genreList[k][1]);
                intent.putExtra("genre3",genreList[k][2]);
                intent.putExtra("genre4",genreList[k][3]);
                intent.putExtra("genre5",genreList[k][4]);
                genre.setText("genre: "+genreList[k][0]+genreList[k][1]+genreList[k][2]+genreList[k][3]+genreList[k][4]);

                intent.putExtra("rating",ratings[k]);
                intent.putExtra("date",dates[k]);
                intent.putExtra("runtime",runtimes[k]);
                intent.putExtra("actor",actors[k]);

                startActivityForResult(intent,0);
            }
        });
        searched.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),detailPage.class);
                intent.putExtra("title",searchList2[i]);
                int k=0;
                for(k=0;k<100;k++){
                    if(movieList[k]==searchList2[i])break;
                }
                intent.putExtra("tagline",taglines[k]);
                intent.putExtra("overview",overviews[k]);

                title.setText(searchList2[i]);
                tagline.setText(taglines[k]);
                overView.setText(overviews[k]);
                actor.setText(actors[k]);

                for(int j=0;j<5;j++){
                    if(genreList[k][j]==null)
                        genreList[k][j]="";
                }

                intent.putExtra("genre1",genreList[k][0]);
                intent.putExtra("genre2",genreList[k][1]);
                intent.putExtra("genre3",genreList[k][2]);
                intent.putExtra("genre4",genreList[k][3]);
                intent.putExtra("genre5",genreList[k][4]);
                genre.setText("genre: "+genreList[k][0]+"   "+genreList[k][1]+"   "+genreList[k][2]+"   "+genreList[k][3]+"   "+genreList[k][4]);

                intent.putExtra("rating",ratings[k]);
                intent.putExtra("date",dates[k]);
                intent.putExtra("runtime",runtimes[k]);
                intent.putExtra("actor",actors[k]);

                startActivityForResult(intent,0);

            }
        });
    }


    public void readData(InputStream is, int i){
        int n=0;
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line="";
        try {
            reader.readLine();
            if(i==1){ //1은 전체data
                while ((line = reader.readLine()) != null) {
                    Log.d("MyAC","Line: "+line);
                    //split by ','
                    String[] tokens = line.split(",");
                    //read the data
                    dataSample sample = new dataSample();
                    sample.setRelease_date(tokens[3]);
                    sample.setImdb_id((tokens[2]));
                    sample.setRuntime(Integer.parseInt(tokens[4]));
                    sample.setTitle(tokens[5]);
                    sample.setVote_average(Double.parseDouble(tokens[6]));
                    sample.setVote_count(Integer.parseInt(tokens[7]));

                    dataSamples.add(sample);


                    if(n<100){
                        vote_avg[n][1]=sample.getVote_average();
                    }
                    String result = sample.getRelease_date().substring(1, sample.getRelease_date().length() - 7);
                    dates[n]=result;
                    result = sample.getImdb_id().substring(1, sample.getImdb_id().length() - 1);
                    imdb[n]=result;
                    runtimes[n]=sample.getRuntime();
                    ratings[n]=sample.getVote_average();
                    result = sample.getTitle().substring(1, sample.getTitle().length() - 1);
                    movieList[n++] = (result);


                    Log.d("MyActivity","just Created"+sample);
                }
            }
            else if(i==2){ //2일때 title로 장르 추출
                n=0;
                int a=0, b=0;
                int action=0, animation=0, adventure=0, crime=0, comedy=0,
                        drama=0, fantasy=0, family=0, thriller=0, mystery=0,
                        science=0, war=0, romance=0;
                oneColData onecol = new oneColData();
                String[] preList=new String[5];
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\""); //작품별 장르들 나눔
                    onecol.setData(tokens[3]);
                    String result = onecol.getData();
                    if(a<100&&b<4){
                        String[] tokens1 = line.split(", "); // 장르 하나하나 나눔
                        preList = result.split(",");
                        while(b< preList.length){
                            genreList[a][b]=preList[b];
                            switch(preList[b]){
                                case "Action":
                                    Action.add(movieList[a]); break;
                                case "Animation":
                                    Animation.add(movieList[a]); break;
                                case "Adventure":
                                    Adventure.add(movieList[a]); break;
                                case "Crime":
                                    Crime.add(movieList[a]); break;
                                case "Comedy":
                                    Comedy.add(movieList[a]); break;
                                case "Drama":
                                    Drama.add(movieList[a]); break;
                                case "Fantasy":
                                    Fantasy.add(movieList[a]); break;
                                case "Family":
                                    Family.add(movieList[a]); break;
                                case "Thriller":
                                    Thriller.add(movieList[a]); break;
                                case "Mystery":
                                    Mystery.add(movieList[a]); break;
                                case "Science Fiction":
                                    ScienceFiction.add(movieList[a]); break;
                                case "War":
                                    War.add(movieList[a]); break;
                                case "Romance":
                                    Romance.add(movieList[a]); break;
                            }
                            b++;
                        }
                        b=0;
                        a++;
                    }
                }


            }
            else if(i==3){
                n=0;
                oneColData onecol = new oneColData();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\""); //작품별 장르들 나눔
                    onecol.setData(tokens[3]);
                    overviews[n++] = onecol.getData();
                }
            }
            else if(i==4){
                n=0;
                oneColData onecol = new oneColData();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\""); //작품별 장르들 나눔
                    onecol.setData(tokens[3]);
                    taglines[n++] = onecol.getData();
                    Log.d("tagtlat","tagline: "+n+onecol.getData());
                }
            }
            else if(i==5){
                n=0;
                oneColData onecol = new oneColData();
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split("\""); //배우목록 받음
                    onecol.setData(tokens[5]);
                    actors[n++] = onecol.getData();
                }
            }
        } catch (IOException e) {
            Log.wtf("MyActivity","Error reading data file on line "+line,e);
            e.printStackTrace();
        }


    }
    public class DetailAdapter extends BaseAdapter {
        Context context;
        String[] movieList =new String[100];
        int poster;
        LayoutInflater inflter;

        public DetailAdapter(Context applicationContext, String movieList[], int poster) {
            this.context = context;
            this.movieList = movieList;
            this.poster = poster;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return movieList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.detail_list, null);
            TextView country = (TextView)           view.findViewById(R.id.textView);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            country.setText(movieList[i]);
            icon.setImageResource(poster);
            return view;
        }



    }
    public class CustomAdapter extends BaseAdapter {
        Context context;
        String[] movieList =new String[100];
        int poster;
        LayoutInflater inflter;

        public CustomAdapter(Context applicationContext, String movieList[], int poster) {
            this.context = context;
            this.movieList = movieList;
            this.poster = poster;
            inflter = (LayoutInflater.from(applicationContext));
        }

        @Override
        public int getCount() {
            return movieList.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = inflter.inflate(R.layout.activity_lists, null);
            TextView country = (TextView)           view.findViewById(R.id.textView);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            country.setText(movieList[i]);
            icon.setImageResource(poster);
            return view;
        }



}
    public class myDBHelper extends SQLiteOpenHelper{
        public myDBHelper(Context context){
            super(context, "groupDB",null,1);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL("CREATE TABLE groupTBL ( gName CHAR(20) PRIMARY KEY, gNumber INTEGER);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }
    }
}
