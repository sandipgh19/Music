package com.example.sandipghosh.music;

/**
 * Created by sandipghosh on 20/03/17.
 */

public class Album1 {

    private String name;
    private int thumb;

   public Album1() {

   }

   public Album1(String name, int thumb) {
       this.name = name;
       this.thumb = thumb;

   }

    public int getThumb() {
        return thumb;
    }

    public void setThumb(int thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
