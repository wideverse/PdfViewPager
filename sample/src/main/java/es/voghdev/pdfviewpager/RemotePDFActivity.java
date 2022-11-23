/*
 * Copyright (C) 2016 Olmo Gallegos Hernández.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package es.voghdev.pdfviewpager;

import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;
import static android.view.KeyEvent.KEYCODE_MENU;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;
import es.voghdev.pdfviewpager.library.util.FileUtil;

public class RemotePDFActivity extends BaseSampleActivity implements DownloadFile.Listener {
    LinearLayout root;
    RemotePDFViewPager remotePDFViewPager;
    EditText etPdfUrl;
    Button btnDownload;
    PDFPagerAdapter adapter;

    Float defaultPdfMaxScale = 7.0f;
    Float defaultPdfMinScale = 1.0f;
    Float navigationDelta = 25f;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.remote_pdf_example);
        setContentView(R.layout.activity_remote_pdf);

        root = findViewById(R.id.remote_pdf_root);
        etPdfUrl = findViewById(R.id.et_pdfUrl);
        btnDownload = findViewById(R.id.btn_download);

        setDownloadButtonListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (adapter != null) {
            adapter.close();
        }
    }

    protected void setDownloadButtonListener() {
        final Context ctx = this;
        final DownloadFile.Listener listener = this;
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remotePDFViewPager = new RemotePDFViewPager(ctx, getUrlFromEditText(), listener);
                remotePDFViewPager.setId(R.id.pdfViewPager);
                hideDownloadButton();
            }
        });
    }

    protected String getUrlFromEditText() {
        return etPdfUrl.getText().toString().trim();
    }

    public void showDownloadButton() {
        btnDownload.setVisibility(View.VISIBLE);
    }

    public void hideDownloadButton() {
        btnDownload.setVisibility(View.INVISIBLE);
    }

    public void updateLayout() {
        root.removeAllViewsInLayout();
        root.addView(etPdfUrl,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(btnDownload,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        root.addView(remotePDFViewPager,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        adapter = new PDFPagerAdapter(this, FileUtil.extractFileNameFromURL(url));
        remotePDFViewPager.setAdapter(adapter);
        updateLayout();
        showDownloadButton();
    }

    @Override
    public void onFailure(Exception e) {
        e.printStackTrace();
        showDownloadButton();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }

    // zoom and navigation gestures for vuzix
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KEYCODE_MENU:
                return zoomIn();
            case KEYCODE_DPAD_RIGHT:
                return moveToRight();
            case KEYCODE_DPAD_LEFT:
                return moveToLeft();
            case KEYCODE_DPAD_UP:
                return moveUp();
            case KEYCODE_DPAD_DOWN:
                return moveDown();
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private boolean zoomIn() {
        Log.d("test-gestures", "zoom in");
        if (remotePDFViewPager != null) {
            if (remotePDFViewPager.getScaleX() == remotePDFViewPager.getScaleY()
                    && remotePDFViewPager.getScaleX() < defaultPdfMaxScale - 1.5f) {
                remotePDFViewPager.setScaleX(remotePDFViewPager.getScaleX() + 1.5f);
                remotePDFViewPager.setScaleY(remotePDFViewPager.getScaleY() + 1.5f);
            } else {
                remotePDFViewPager.setScaleX(defaultPdfMinScale);
                remotePDFViewPager.setScaleY(defaultPdfMinScale);
            }
            return true;
        }
        return false;
    }

    private boolean moveUp() {
        if (remotePDFViewPager != null) {
            remotePDFViewPager.setPivotY(remotePDFViewPager.getPivotY() - navigationDelta);
            return true;
        }
        return false;
    }

    private boolean moveDown() {
        if (remotePDFViewPager != null) {
            remotePDFViewPager.setPivotY(remotePDFViewPager.getPivotY() + navigationDelta);
            return true;
        }
        return false;
    }

    public boolean moveToRight() {
        if (remotePDFViewPager != null) {
            remotePDFViewPager.setPivotX(remotePDFViewPager.getPivotX() + navigationDelta);
            return true;
        }
        return false;
    }

    public boolean moveToLeft() {
        if (remotePDFViewPager != null) {
            remotePDFViewPager.setPivotX(remotePDFViewPager.getPivotX() - navigationDelta);
            return true;
        }
        return false;
    }

}

