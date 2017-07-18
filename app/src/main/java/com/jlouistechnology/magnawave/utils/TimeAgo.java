package com.jlouistechnology.magnawave.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.jlouistechnology.magnawave.R;

import java.util.Date;

/**
 * Created by aipxperts on 16/5/17.
 */

public class TimeAgo {

    protected Context context;

    public TimeAgo(Context context) {
        this.context = context;
    }

    public String timeAgo(Date date) {
        return timeAgo(date.getTime());
    }


    public String timeAgo(long millis) {
        long diff = new Date().getTime() - (millis*1000);

        Resources r = context.getResources();

        String prefix = r.getString(R.string.time_ago_prefix);
        String suffix = r.getString(R.string.time_ago_suffix);

        double seconds = Math.abs(diff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double years = days / 365;

        String words;

        if (seconds < 45) {
            words = r.getString(R.string.time_ago_seconds, Math.round(seconds));
            words="Today";
        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_minute, 1);
            words="Today";
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minutes, Math.round(minutes));
            words="Today";
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hour, 1);
            words="Today";
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, Math.round(hours));
            Date dateTime_entry = new Date(millis*1000);
            Date dateTime_current = new Date(new Date().getTime());
            int day_entry=dateTime_entry.getDay();
            int day_current=dateTime_current.getDay();
            Log.e("day",day_current+" "+day_entry);
            if(day_current!=day_entry)
            {
                words="Yesterday";
            }
            else {
                words="Today";
            }
        } else if (hours < 42) {
            words = r.getString(R.string.time_ago_day, 1);
            words="Yesterday";

        }else if (days < 8) {
            words = r.getString(R.string.time_ago_month, 1);
            words="This Week";
        }
        else if (days < 30) {
            words = r.getString(R.string.time_ago_days, Math.round(days));
            words="This Month";
        }
        else if (days < 45) {
            words = r.getString(R.string.time_ago_month, 1);
            words="This Year";
        } else if (days < 365) {
            words = r.getString(R.string.time_ago_months, Math.round(days / 30));
            words="This Year";
        } else if (years < 1.5) {
            words = r.getString(R.string.time_ago_year, 1);
            words="Before this Year";
        } else {
            words = r.getString(R.string.time_ago_years, Math.round(years));
            words="Before this Year";
        }

        StringBuilder sb = new StringBuilder();

        if (prefix != null && prefix.length() > 0) {
            sb.append(prefix).append(" ");
        }

        sb.append(words);

        /*if (suffix != null && suffix.length() > 0) {
            sb.append(" ").append(suffix);
        }*/

        return sb.toString().trim();
    }


}