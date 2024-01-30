package com.github.tvbox.osc.util;

import android.net.Uri;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class VideoParseRuler {

    private static final HashMap<String, ArrayList<ArrayList<String>>> HOSTS_RULE = new HashMap<>();
    private static final HashMap<String, ArrayList<ArrayList<String>>> HOSTS_FILTER = new HashMap<>();

    public static void clearRule() {
        HOSTS_RULE.clear();
        HOSTS_FILTER.clear();
    }

    public static void addHostRule(String host, ArrayList<String> rule) {
        ArrayList<ArrayList<String>> rules = new ArrayList<>();
        if (HOSTS_RULE.get(host) != null && HOSTS_RULE.get(host).size() > 0) {
            rules = HOSTS_RULE.get(host);
        }
        rules.add(rule);
        HOSTS_RULE.put(host, rules);
    }

    public static ArrayList<ArrayList<String>> getHostRules(String host) {
        if (HOSTS_RULE.containsKey(host)) {
            return HOSTS_RULE.get(host);
        }
        return null;
    }

    public static void addHostFilter(String host, ArrayList<String> rule) {
        ArrayList<ArrayList<String>> filters = new ArrayList<>();
        if (HOSTS_FILTER.get(host) != null && HOSTS_FILTER.get(host).size() > 0) {
            filters = HOSTS_FILTER.get(host);
        }
        filters.add(rule);
        HOSTS_FILTER.put(host, filters);
    }

    public static ArrayList<ArrayList<String>> getHostFilters(String host) {
        if (HOSTS_FILTER.containsKey(host)) {
            return HOSTS_FILTER.get(host);
        }
        return null;
    }

    public static boolean checkIsVideoForParse(String webUrl, String url) {
        try {
            boolean isVideo = DefaultConfig.isVideoFormat(url);
            if (!HOSTS_RULE.isEmpty() && !isVideo && webUrl != null) {
                Uri uri = Uri.parse(webUrl);
                if(getHostRules(uri.getHost()) != null){
                    isVideo = checkVideoForOneHostRules(uri.getHost(), url);
                }else {
                    isVideo = checkVideoForOneHostRules("*", url);
                }
            }
            return isVideo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean checkVideoForOneHostRules(String host, String url) {
        boolean isVideo = false;
        ArrayList<ArrayList<String>> hostRules = getHostRules(host);
        if (hostRules != null && hostRules.size() > 0) {
            boolean isVideoRuleCheck = false;
            for(int i=0; i<hostRules.size(); i++) {
                boolean checkIsVideo = true;
                if (hostRules.get(i) != null && hostRules.get(i).size() > 0) {
                    for(int j=0; j<hostRules.get(i).size(); j++) {
                        Pattern onePattern = Pattern.compile("" + hostRules.get(i).get(j));
                        if (!onePattern.matcher(url).find()) {
                            checkIsVideo = false;
                            break;
                        }
                        LOG.i("VIDEO RULE:" + hostRules.get(i).get(j));
                    }
                } else {
                    checkIsVideo = false;
                }
                if (checkIsVideo) {
                    isVideoRuleCheck = true;
                    break;
                }
            }
            if (isVideoRuleCheck) {
                isVideo = true;
            }
        }
        return isVideo;
    }

    public static boolean isFilter(String webUrl, String url) {
        try {
            boolean isFilter = false;
            if (!HOSTS_FILTER.isEmpty() && webUrl != null) {
                Uri uri = Uri.parse(webUrl);
                if(getHostFilters(uri.getHost()) != null){
                    isFilter = checkIsFilterForOneHostRules(uri.getHost(), url);
                }
            }
            return isFilter;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean checkIsFilterForOneHostRules(String host, String url) {
        boolean isFilter = false;
        ArrayList<ArrayList<String>> hostFilters = getHostFilters(host);
        if (hostFilters != null && hostFilters.size() > 0) {
            boolean isFilterRuleCheck = false;
            for(int i=0; i<hostFilters.size(); i++) {
                boolean checkIsFilter = true;
                if (hostFilters.get(i) != null && hostFilters.get(i).size() > 0) {
                    for(int j=0; j<hostFilters.get(i).size(); j++) {
                        Pattern onePattern = Pattern.compile("" + hostFilters.get(i).get(j));
                        if (!onePattern.matcher(url).find()) {
                            checkIsFilter = false;
                            break;
                        }
                        LOG.i("FILTER RULE:" + hostFilters.get(i).get(j));
                    }
                } else {
                    checkIsFilter = false;
                }
                if (checkIsFilter) {
                    isFilterRuleCheck = true;
                    break;
                }
            }
            if (isFilterRuleCheck) {
                isFilter = true;
            }
        }
        return isFilter;
    }



}
