package com.example.playground.ui.dragndrop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.playground.BaseActivity;
import com.example.playground.R;

public class DragDropActivity extends BaseActivity implements View.OnDragListener, View.OnLongClickListener {

    private TextView tvDrag;
    private ImageView imgDrag;
    private Button btnDrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_drop);

        tvDrag = findViewById(R.id.tvDrag);
        tvDrag.setTag("DRAGGABLE TEXTVIEW");
        tvDrag.setOnLongClickListener(this);
        imgDrag = findViewById(R.id.imgDrag);
        imgDrag.setTag("DRAGGABLE IMAGEVIEW");
        imgDrag.setOnLongClickListener(this);
        btnDrag = findViewById(R.id.btnDrag);
        btnDrag.setTag("DRAGGABLE BUTTON");
        btnDrag.setOnLongClickListener(this);

        findViewById(R.id.llPertama).setOnDragListener(this);
        findViewById(R.id.llKedua).setOnDragListener(this);
        findViewById(R.id.llKetiga).setOnDragListener(this);
    }

    @Override
    public boolean onDrag(View view, DragEvent dragEvent) {
        int action = dragEvent.getAction();

        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED:
                if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    return true;
                }

                return false;

            case DragEvent.ACTION_DRAG_ENTERED:
                view.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
                view.invalidate();

                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                view.getBackground().clearColorFilter();
                view.invalidate();
                return true;

            case DragEvent.ACTION_DROP:
                ClipData.Item item = dragEvent.getClipData().getItemAt(0);

                String dragData = item.getText().toString();

                Toast.makeText(this, "Dragged Data is: " + dragData, Toast.LENGTH_SHORT).show();

                view.getBackground().clearColorFilter();

                view.invalidate();

                View vw = (View) dragEvent.getLocalState();
                ViewGroup owner = (ViewGroup) vw.getParent();
                owner.removeView(vw); // Remove dragged view

                LinearLayout container = (LinearLayout) view;
                container.addView(vw);
                vw.setVisibility(View.VISIBLE);

                return true;

            case DragEvent.ACTION_DRAG_ENDED:

                view.getBackground().clearColorFilter();
                view.invalidate();

                if (dragEvent.getResult()) {
                    Toast.makeText(this, "The Drop was Handled", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "The drop didn't work", Toast.LENGTH_SHORT).show();
                }

                return true;

            default:
                Log.e("DragDrop Exampe", "onDrag: Unknown type of dragging");
                break;
        }

        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        // Create Clip Data
        ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
        String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
        ClipData data = new ClipData(view.getTag().toString(), mimeTypes, item);

        // Instantiates the drag shadow builder
        View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);

        // Start Drag
        view.startDrag(data, dragShadowBuilder, view, 0);

        return true;
    }
}