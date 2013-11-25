package com.surevine.community.gateway.model.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
* @author rich
*/
public class CircularFIFOQueue<E> implements Queue<E> {

 private int maxSize;

 private List<E> storage;

 public CircularFIFOQueue(final int maxSize) {
   this.maxSize = maxSize;

   storage = new LinkedList<E>();
 }

 @Override
 public boolean addAll(final Collection<? extends E> items) {
   boolean returnValue = true;

   for (final E item : items) {
     returnValue &= add(item);
   }

   return returnValue;
 }

 @Override
 public void clear() {
   storage.clear();
 }

 @Override
 public boolean contains(final Object item) {
   return storage.contains(item);
 }

 @Override
 public boolean containsAll(final Collection<?> items) {
   return storage.containsAll(items);
 }

 @Override
 public boolean isEmpty() {
   return storage.isEmpty();
 }

 @Override
 public Iterator<E> iterator() {
   return storage.iterator();
 }

 @Override
 public boolean remove(final Object item) {
   return storage.remove(item);
 }

 @Override
 public boolean removeAll(final Collection<?> items) {
   return storage.removeAll(items);
 }

 @Override
 public boolean retainAll(final Collection<?> items) {
   return storage.retainAll(items);
 }

 @Override
 public int size() {
   return storage.size();
 }

 @SuppressWarnings("unchecked")
 @Override
 public E[] toArray() {
   return (E[]) storage.toArray();
 }

 @Override
 public <T> T[] toArray(final T[] items) {
   return storage.toArray(items);
 }

 @Override
 public boolean add(final E item) {
   if (storage.contains(item)) {
     storage.remove(item);
   } else {
     if (storage.size() >= maxSize) {
       remove();
     }
   }

   return storage.add(item);
 }

 @Override
 public E element() {
   return storage.get(0);
 }

 @Override
 public boolean offer(final E item) {
   return add(item);
 }

 @Override
 public E peek() {
   return element();
 }

 @Override
 public E poll() {
   if (storage.size() == 0) {
     return null;
   } else {
     return peek();
   }
 }

 @Override
 public E remove() {
   final E item = storage.get(0);

   storage.remove(item);

   return item;
 }
}