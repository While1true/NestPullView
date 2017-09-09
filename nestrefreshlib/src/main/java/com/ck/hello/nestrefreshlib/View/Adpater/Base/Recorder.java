package com.ck.hello.nestrefreshlib.View.Adpater.Base;

import com.ck.hello.nestrefreshlib.R;

/**
 * Created by ck on 2017/9/9.
 */

public class Recorder {
    private int Loadingres = R.layout.sbase_loading;
    private int emptyres = R.layout.empty_textview;
    private int errorres = R.layout.network_error;
    private int nomore = R.layout.nomore;

    private Recorder() {
    }

    public int getNomore() {
        return nomore;
    }

    private void setNomore(int nomore) {
        this.nomore = nomore;
    }

    private void setLoadingres(int loadingres) {
        Loadingres = loadingres;
    }

    private void setEmptyres(int emptyres) {
        this.emptyres = emptyres;
    }


    private void setErrorres(int errorres) {
        this.errorres = errorres;
    }

    public int getLoadingres() {
        return Loadingres;
    }

    public int getEmptyres() {
        return emptyres;
    }


    public int getErrorres() {
        return errorres;
    }

    @Override
    public String toString() {
        return "Recorder{" +
                "Loadingres=" + Loadingres +
                ", emptyres=" + emptyres +
                ", contentres=" +
                ", errorres=" + errorres +
                '}';
    }
    public static class Builder {
        private int Loadingres = R.layout.sbase_loading;
        private int emptyres = R.layout.empty_textview;
        private int errorres = R.layout.network_error;
        private int nomore = R.layout.nomore;



         public Builder setNomoreRes(int res) {
            this.nomore = res;
             return this;
        }

        public Builder setLoadingRes(int res) {
            this.Loadingres = res;
            return this;
        }

        public Builder setEmptyRes(int res) {
            this.emptyres = res;
            return this;
        }

        public Builder setErrorRes(int res) {
            this.errorres = res;
            return this;
        }

        public Recorder build() {
            Recorder recorder = new Recorder();
            recorder.setLoadingres(Loadingres);
            recorder.setEmptyres(emptyres);
            recorder.setErrorres(errorres);
            recorder.setNomore(nomore);
            return recorder;
        }

    }
}
