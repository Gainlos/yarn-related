package com.csob.mapreduce.csvcount;


import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * KEYIN, reduce阶段输入的key的类型：Text
 * VALUEIN, reduce阶段输入的value的类型： IntWritable
 * KEYOUT, reduce阶段输出的key的类型： Text
 * VALUEOUT, reduce阶段输出的value的类型: IntWritable
 *
 */
public class CsvcountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    IntWritable outV = new IntWritable();
    @Override
    protected void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException,InterruptedException{
        int sum=0;
        for (IntWritable value : values) {
            sum+=value.get();
        }
        outV.set(sum);
        context.write(key,outV);
    }
}
