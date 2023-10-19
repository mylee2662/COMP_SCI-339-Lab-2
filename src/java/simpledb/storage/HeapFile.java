package simpledb.storage;

import simpledb.common.Database;
import simpledb.common.DbException;
import simpledb.common.Debug;
import simpledb.common.Permissions;
import simpledb.transaction.TransactionAbortedException;
import simpledb.transaction.TransactionId;

import java.io.*;
import java.util.*;

/**
 * HeapFile is an implementation of a DbFile that stores a collection of tuples
 * in no particular order. Tuples are stored on pages, each of which is a fixed
 * size, and the file is simply a collection of those pages. HeapFile works
 * closely with HeapPage. The format of HeapPages is described in the HeapPage
 * constructor.
 * 
 * @see HeapPage#HeapPage
 * @author Sam Madden
 */
public class HeapFile implements DbFile {

    public class HeapFileIterator extends AbstractDbFileIterator{
        private HeapPage currPage = null;
        private Iterator<Tuple> curPageTuples;
        public TransactionId tid;
        public HeapFileIterator(TransactionId tid){
            this.tid = tid;
        }
        public Tuple readNext() throws DbException, TransactionAbortedException {
            if (currPage == null) {
                return null;
            }

            if (curPageTuples == null || !curPageTuples.hasNext()) {
                int nextPageNum = currPage.getId().getPageNumber() + 1;
                if (nextPageNum >= numPages()) {
                    return null;
                }
                HeapPageId curPid = new HeapPageId(getId(), nextPageNum);
                currPage = (HeapPage) Database.getBufferPool().getPage(tid, curPid, Permissions.READ_ONLY);

                curPageTuples = currPage.iterator();

            }
            return curPageTuples.next();
        }

        public void open() throws DbException, TransactionAbortedException{
            HeapPageId curPid = new HeapPageId(getId(), 0);
            currPage = (HeapPage) Database.getBufferPool().getPage(tid, curPid, Permissions.READ_ONLY);
            curPageTuples = currPage.iterator();
        }

        public void rewind() throws DbException, TransactionAbortedException {
            close();
            open();
        }

        @Override public void close() {
            // Ensures that a future call to next() will fail
            super.close();
            currPage = null;
            curPageTuples = null;
        }
    }

    private File file;
    private TupleDesc tupleDesc;

    /**
     * Constructs a heap file backed by the specified file.
     * 
     * @param f
     *            the file that stores the on-disk backing store for this heap
     *            file.
     */
    public HeapFile(File f, TupleDesc td) {
        // some code goes here
        file = f;
        tupleDesc = td;
    }

    /**
     * Returns the File backing this HeapFile on disk.
     * 
     * @return the File backing this HeapFile on disk.
     */
    public File getFile() {
        // some code goes here
        return file;
    }

    /**
     * Returns an ID uniquely identifying this HeapFile. Implementation note:
     * you will need to generate this tableid somewhere to ensure that each
     * HeapFile has a "unique id," and that you always return the same value for
     * a particular HeapFile. We suggest hashing the absolute file name of the
     * file underlying the heapfile, i.e. f.getAbsoluteFile().hashCode().
     * 
     * @return an ID uniquely identifying this HeapFile.
     */
    public int getId() {
        // some code goes here
        return file.getAbsoluteFile().hashCode();
    }

    /**
     * Returns the TupleDesc of the table stored in this DbFile.
     * 
     * @return TupleDesc of this DbFile.
     */
    public TupleDesc getTupleDesc() {
        // some code goes here
        return tupleDesc;
    }

    // see DbFile.java for javadocs
    public Page readPage(PageId pid) {
        // some code goes here
        int offset = BufferPool.getPageSize() * pid.getPageNumber();
        int len = BufferPool.getPageSize();
        byte[] data = new byte[len];

        try{
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            raf.skipBytes(offset);
            raf.read(data, 0, len);

            HeapPage page = new HeapPage((HeapPageId) pid, data);
            raf.close();

            return page;
        }
        catch (IOException e){
            Debug.log(0, e.getMessage());
            return null;
        }
    }

    // see DbFile.java for javadocs
    public void writePage(Page page) throws IOException {
        // some code goes here
        // not necessary for lab1
    }

    /**
     * Returns the number of pages in this HeapFile.
     */
    public int numPages() {
        // some code goes here
        return (int) (file.length()/BufferPool.getPageSize());
    }

    // see DbFile.java for javadocs
    public List<Page> insertTuple(TransactionId tid, Tuple t)
            throws DbException, IOException, TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public List<Page> deleteTuple(TransactionId tid, Tuple t) throws DbException,
            TransactionAbortedException {
        // some code goes here
        return null;
        // not necessary for lab1
    }

    // see DbFile.java for javadocs
    public DbFileIterator iterator(TransactionId tid) {
        // some code goes here
        return new HeapFileIterator(tid);
    }

}

