package hansung.ac.myfinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class detailPage extends AppCompatActivity{

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.detail);
            setTitle("상세보기 페이지");

            Intent inIntent = getIntent();
            String getTitle = inIntent.getStringExtra("title");
            String getTagline = inIntent.getStringExtra("tagline");
            String getOverview = inIntent.getStringExtra("overview");
            String getGenre1 = inIntent.getStringExtra("genre1");
            String getGenre2 = inIntent.getStringExtra("genre2");
            String getGenre3 = inIntent.getStringExtra("genre3");
            String getGenre4 = inIntent.getStringExtra("genre4");
            String getGenre5 = inIntent.getStringExtra("genre5");
            Double getRating = inIntent.getDoubleExtra("rating",0);
            String getDate = inIntent.getStringExtra("date");
            int getRuntime = inIntent.getIntExtra("runtime",0);
            String getActor = inIntent.getStringExtra("actor");

            TextView title = (TextView) findViewById(R.id.title);
            TextView overView = (TextView) findViewById(R.id.overview);
            TextView tagline = (TextView) findViewById(R.id.tagline);
            TextView genre = (TextView)findViewById(R.id.genre);
            TextView rating = (TextView)findViewById(R.id.rating);
            TextView date = (TextView)findViewById(R.id.date);
            TextView runtime = (TextView)findViewById(R.id.runtime);
            TextView actor = (TextView)findViewById(R.id.actor);

            title.setText(getTitle);
            overView.setText(getOverview);
            tagline.setText(getTagline);
            genre.setText("genre: "+getGenre1+getGenre2+getGenre3+getGenre4+getGenre5);
            rating.setText("★ "+getRating);
            date.setText("|  "+getDate);
            runtime.setText("|  "+getRuntime+"min");
            actor.setText(getActor);



            Button btn;
            btn = (Button) findViewById(R.id.btnBack);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });


        }

}
