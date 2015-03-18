package com.ctrip.infosec.flowtable4j.service;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

/**
 * Created by zhangsx on 2015/3/18.
 */
public class SortTest {
    @Test
    @Ignore
    public void testGroupby(){
        List<Entry> list = new ArrayList<Entry>();
        Entry entry0 = new Entry();
        entry0.setKey("a");
        entry0.setValue("12");
        Entry entry1 = new Entry();
        entry1.setKey("b");
        entry1.setValue("1e2");
        Entry entry2 = new Entry();
        entry2.setKey("b");
        entry2.setValue("1d2");
        Entry entry3 = new Entry();
        entry3.setKey("a");
        entry3.setValue("12");
        Entry entry4 = new Entry();
        entry4.setKey("c");
        entry4.setValue("12as");
        Entry entry5 = new Entry();
        entry5.setKey("d");
        entry5.setValue("12vfe");
        list.add(entry0);
        list.add(entry1);
        list.add(entry2);
        list.add(entry3);
        list.add(entry4);
        list.add(entry5);

        HashMap<String,List<Entry>> group = new HashMap<String, List<Entry>>();
        for(Entry entry:list){
            if(group.containsKey(entry.getKey())){
                group.get(entry.getKey()).add(entry);
            }else{
                List<Entry> group_list = new ArrayList<Entry>();
                group_list.add(entry);
                group.put(entry.getKey(),group_list);
            }
        }
        Collections.sort(list, new Comparator<Entry>() {
            @Override
            public int compare(Entry o1, Entry o2) {
                if(o1.getKey().equals(o2.getKey())){
//                    if(group.containsKey(o1.getKey())){
//                        group.get(o1.getKey()).add(o1);
//                        group.get(o1.getKey()).add(o2);
//                    }else{
//                        List<Entry> list = new ArrayList<Entry>();
//                        list.add(o1);
//                        list.add(o2);
//                        group.put(o1.getKey(),list);
//                    }
                    return 0;
                }else if(o1.getKey().compareTo(o2.getKey())>0){
                    return 1;
                }else{
                    return -1;
                }
            }
        });

        System.out.println(">>>list");
        for(Entry e:list){
            System.out.println("key:"+e.getKey()+",value:"+e.getValue());
        }
        System.out.println("<<<");

        System.out.println(">>>group");
        for(Iterator<String> it=group.keySet().<String>iterator();it.hasNext();){
            String key= it.next();
            for(Entry entry:group.get(key)){
                System.out.println("key:"+entry.getKey()+",value:"+entry.getValue());
            }
        }
        System.out.println("<<<");
    }
    public void testOrderby(){

    }

    static class Entry{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
