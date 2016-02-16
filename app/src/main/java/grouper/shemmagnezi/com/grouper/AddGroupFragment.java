package grouper.shemmagnezi.com.grouper;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddGroupFragment extends Fragment {

    public interface AddGroupFragmentListener {
        void cancel();
        void addGroup(String name);
    }

    private AddGroupFragmentListener listener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_group, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        View add = view.findViewById(R.id.add_group_add);
        add.getBackground().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addGroup(((EditText) view.findViewById(R.id.add_group_name)).getText().toString());
            }
        });

        View cancel = view.findViewById(R.id.add_group_cancel);
        cancel.getBackground().setColorFilter(new PorterDuffColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.cancel();
            }
        });


        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                View window = view.findViewById(R.id.add_group_window);
                window.setTranslationY(window.getHeight());
                AnimatorSet set = new AnimatorSet();
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
                anim1.setDuration(400);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(window, "translationY", window.getHeight(), 0);
                anim2.setDuration(200);
                set.playSequentially(anim1, anim2);
                set.start();

                return false;
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddGroupFragmentListener) context;
    }
}
