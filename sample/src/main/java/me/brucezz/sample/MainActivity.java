package me.brucezz.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCardStackView = (CardStackView) findViewById(R.id.card_stack_view);
        mCardStackView.setOnCardClickListener(new CardStackView.onCardClickListener() {
            @Override
            public void onClick(View view, int realIndex, int initialIndex) {
                Toast.makeText(MainActivity.this, "点击了第" + realIndex + "个卡片 => " + mCards.get(initialIndex).mTitle,
                    Toast.LENGTH_SHORT).show();
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
        menu.add(1, 1, 1, "修改数据");
        menu.add(1, 2, 1, "重置");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                for (Card card : mCards) {
                    card.mTitle += "X";
                }
                mCardAdapter.notifyDataSetChanged();
                break;
            case 2:
                recreate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
