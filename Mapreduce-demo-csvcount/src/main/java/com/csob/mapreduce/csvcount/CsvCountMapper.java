package com.csob.mapreduce.csvcount;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * KEYIN, map阶段输入的key的类型：LongWritable
 * VALUEIN, map阶段输入的value的类型： Text
 * KEYOUT, map阶段输出的key的类型： Text
 * VALUEOUT, map阶段输出的value的类型: IntWritable
 *
 */
public class CsvCountMapper extends Mapper<LongWritable, Text,Text, IntWritable>{
    Text outK = new Text();
    IntWritable one= new IntWritable(1);
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException,InterruptedException{
        // 1 获取一行
        String line = value.toString();
        //2 切割
        String[] words = line.split(",");
        //3 循环写出
        for (String word : words) {
            outK.set(word);
            context.write(outK,one);

        }
    }
}
