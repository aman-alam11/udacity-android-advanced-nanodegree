package neu.droid.guy.watchify.POJO;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class ListOfMovies implements Parcelable{

    private List<Movie> results;


    public List<Movie> getResults(){
        return this.results;
    }

    protected ListOfMovies(Parcel in) {
    }

    public static final Creator<ListOfMovies> CREATOR = new Creator<ListOfMovies>() {
        @Override
        public ListOfMovies createFromParcel(Parcel in) {
            return new ListOfMovies(in);
        }

        @Override
        public ListOfMovies[] newArray(int size) {
            return new ListOfMovies[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
