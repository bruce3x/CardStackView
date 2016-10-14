package me.brucezz.sample;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import me.brucezz.cardstackview.CardAdapter;

/**
 * Created by brucezz on 2016-10-12.
 * Github: https://github.com/brucezz
 * Email: im.brucezz@gmail.com
 */

public class SimpleCardAdapter extends CardAdapter {

    private Context mContext;
    private List<Card> mCards;

    public SimpleCardAdapter(Context context, List<Card> cards) {
        mContext = context;
        mCards = cards;
    }

    @Override
    public View getView(int position, ViewGroup parent) {
        Card card = mCards.get(position);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);
        CardView cardView = (CardView) view.findViewById(R.id.card);
        TextView title = (TextView) view.findViewById(R.id.card_title);
        ImageView image = (ImageView) view.findViewById(R.id.card_image);

        title.setText(card.mTitle);
        image.setImageResource(card.mImage);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // BugFix: 4.x 版本: CardView 中填充 ImageView 会有白边
            cardView.setCardBackgroundColor(card.mBgColor);
        }
        return view;
    }

    @Override
    public int getItemCount() {
        return mCards != null ? mCards.size() : 0;
    }

    @Override
    public int getCardHeight() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.card_height);
    }

    @Override
    public int getMinCardSpan() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.card_span_min);
    }
}
