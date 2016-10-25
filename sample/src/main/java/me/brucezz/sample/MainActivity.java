package me.brucezz.sample;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.Arrays;
import java.util.List;
import me.brucezz.cardstackview.CardStackView;

public class MainActivity extends AppCompatActivity {

    CardStackView mCardStackView;
    SimpleCardAdapter mCardAdapter;
    private List<Card> mCards;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            modifyData();
            mHandler.sendEmptyMessageDelayed(0, 2000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStackView = (CardStackView) findViewById(R.id.card_stack_view);
        mCardStackView.setOnCardClickListener(new CardStackView.OnCardClickListener() {
            @Override
            public void onClick(View view, int realIndex, int initialIndex) {
                Toast.makeText(MainActivity.this, "点击了第" + realIndex + "个卡片 => " + mCards.get(initialIndex).mTitle,
                    Toast.LENGTH_SHORT).show();
            }
        });
        mCardStackView.setOnPositionChangedListener(new CardStackView.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(List<Integer> position) {
                StringBuilder sb = new StringBuilder();
                for (Integer integer : position) {
                    sb.append(integer).append(" ");
                }
                Log.d("TAG", "onPositionChanged: " + sb.toString());
            }
        });

        mCards = fakeCards();
        mCardAdapter = new SimpleCardAdapter(this, mCards);

        mCardStackView.setAdapter(mCardAdapter);
    }

    private List<Card> fakeCards() {

        return Arrays.asList(new Card(0xFF2196F3, R.drawable.post, "动态"), new Card(0xFF17B084, R.drawable.task, "任务"),
            new Card(0xFFE85D72, R.drawable.calendar, "日程"), new Card(0xFF00BACF, R.drawable.knowledge, "知识"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 1, 1, "定时2s修改数据");
        menu.add(1, 2, 1, "重置");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                mHandler.sendEmptyMessageDelayed(0, 2000);
                break;
            case 2:
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void modifyData() {
        for (int i = 0; i < mCards.size(); i++) {
            mCards.get(i).mTitle += String.valueOf(i);
        }
        mCardAdapter.notifyDataSetChanged();
    }
}
