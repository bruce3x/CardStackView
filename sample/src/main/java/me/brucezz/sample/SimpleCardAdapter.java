package me.brucezz.sample;

import android.content.Context;
import android.os.Build;
import android.support.v4.util.Pair;
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
    private List<Pair<Integer, Card>> mCards;

    public SimpleCardAdapter(Context context, List<Pair<Integer, Card>> cards) {
        mContext = context;
        mCards = cards;
    }

    @Override
    public View getView(View oldView, int position, ViewGroup parent) {
        Card card = mCards.get(position).second;

        ViewHolder holder;
        View view;
        if (oldView != null && oldView.getTag() instanceof ViewHolder) {
            holder = (ViewHolder) oldView.getTag();
            view = oldView;
        } else {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.item_card, parent, false);
            holder.mCardView = (CardView) view.findViewById(R.id.card);
            holder.mTextView = (TextView) view.findViewById(R.id.card_title);
            holder.mImageView = (ImageView) view.findViewById(R.id.card_image);
        }

        holder.mTextView.setText(card.mTitle);
        holder.mImageView.setImageResource(card.mImage);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // BugFix: 4.x 版本: CardView 中填充 ImageView 会有白边
            holder.mCardView.setCardBackgroundColor(card.mBgColor);
        }
        view.setTag(holder);

        return view;
    }

    @Override
    public int getItemCount() {
        return mCards != null ? mCards.size() : 0;
    }

    @Override
    public int getOrder(int position) {
        return mCards.get(position).first;
    }

    private static class ViewHolder {
        CardView mCardView;
        TextView mTextView;
        ImageView mImageView;
    }
}
