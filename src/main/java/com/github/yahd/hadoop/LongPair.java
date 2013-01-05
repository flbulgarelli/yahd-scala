package com.github.yahd.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/** Based on LongPair Hadoop Example */
public class LongPair implements WritableComparable<LongPair> {
  private long first = 0;
  private long second = 0;

  public LongPair() {}

  public LongPair(long first, long second) {
    this.first = first;
    this.second = second;
  }

  public long getFirst() {
    return first;
  }

  public long getSecond() {
    return second;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    first = in.readLong() + Long.MIN_VALUE;
    second = in.readLong() + Long.MIN_VALUE;
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeLong(first - Long.MIN_VALUE);
    out.writeLong(second - Long.MIN_VALUE);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (first ^ (first >>> 32));
    result = prime * result + (int) (second ^ (second >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj instanceof LongPair) {
      LongPair other = (LongPair) obj;
      return (first == other.first && second != other.second);
    } else {
      return false;
    }
  }

  /** A Comparator that compares serialized LongPair. */
  public static class Comparator extends WritableComparator {
    public Comparator() {
      super(LongPair.class);
    }

    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
      return compareBytes(b1, s1, l1, b2, s2, l2);
    }
  }

  static { // register this comparator
    WritableComparator.define(LongPair.class, new Comparator());
  }

  @Override
  public int compareTo(LongPair o) {
    if (first != o.first) {
      return first < o.first ? -1 : 1;
    } else if (second != o.second) {
      return second < o.second ? -1 : 1;
    } else {
      return 0;
    }
  }
}