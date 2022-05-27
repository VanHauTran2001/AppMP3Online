package com.example.appmp3online.Activity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appmp3online.Adapter.AdapterBaiHat;
import com.example.appmp3online.Model.BaiHat;
import com.example.appmp3online.R;
import com.example.appmp3online.databinding.ActivityMainBinding;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements AdapterBaiHat.IListen {
    private ActivityMainBinding binding;

    private static final String url = "https://chiasenhac.vn/tim-kiem?q=Ai+Chung+T%C3%ACnh+%C4%90%C6%B0%E1%BB%A3c+M%C3%A3i&page_music=1&filter=";
    private ArrayList<BaiHat> baiHatArrayList = new ArrayList<>();
    private AdapterBaiHat adapterBaiHat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        adapterBaiHat = new AdapterBaiHat(this);;
        configRecylerView();
        new DowloadTask().execute(url);
        searchSong();
        
    }

    private void searchSong() {
        binding.imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textSearch = binding.edtSearch.getText().toString().trim();
                String urlSearch = "https://chiasenhac.vn/tim-kiem?q="+textSearch;
                configRecylerView();
                new DowloadTask().execute(urlSearch);
            }
        });


    }

    private void configRecylerView() {
        AdapterBaiHat adapterBaiHat = new AdapterBaiHat(this);
        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        binding.recylerBaiHat.setLayoutManager(linearLayoutManager);
        binding.recylerBaiHat.setAdapter(adapterBaiHat);
    }

    @Override
    public int getCount() {
        return baiHatArrayList.size();
    }

    @Override
    public BaiHat getData(int position) {
        return baiHatArrayList.get(position);
    }

    @Override
    public void onClickBaiHat(int position) {
        Intent intent = new Intent(this,PlayNhacActivity.class);
        intent.putExtra("playnhac",baiHatArrayList.get(position));
        startActivity(intent);
        //Bat dau khoi chay service
        startService(intent);
    }

    @Override
    public Context onContext() {
        return getApplicationContext();
    }
    @SuppressLint("StaticFieldLeak")
    private class DowloadTask extends AsyncTask<String,Void,ArrayList<BaiHat>>{

        @Override
        protected ArrayList<BaiHat> doInBackground(String... strings) {
            Document document;
            baiHatArrayList = new ArrayList<>();
            try {

                document = Jsoup.connect(strings[0]).get();

                Elements elements = Objects.requireNonNull(document.select("div.tab-content").first()).select("ul.list_music");
                for (Element e : elements) {
                    Elements elements1 = e.select("li.media");
                    for (Element e1 : elements1) {
                        String name = Objects.requireNonNull(e1.select("a.search_title").first()).attr("title");
//                        String img = Objects.requireNonNull(e1.select("a.search_title").first()).select("img").attr("src");
                        String singer = e1.select("div.media-body").select("div.author").text();
                        String link  = Objects.requireNonNull(e1.select("a.search_title").first()).attr("href");
                        Document document1 = Jsoup.connect(link).get();
                        Element elementLink = document1.select("div.tab-content").first();
                        String linkMP3 = elementLink.select("a.download_item").attr("href");
                        baiHatArrayList.add(new BaiHat(name,singer,linkMP3));

                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            return baiHatArrayList;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(ArrayList<BaiHat> baiHats) {
            super.onPostExecute(baiHats);

//            Setup data recylerView
            Objects.requireNonNull(binding.recylerBaiHat.getAdapter()).notifyDataSetChanged();
        }
    }
}