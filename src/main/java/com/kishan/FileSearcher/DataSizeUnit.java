package com.kishan.FileSearcher;


/**
 * The Enum DataSize.
 */
public enum DataSizeUnit 
{
    
    /** The bytes. */
    BYTES {
        @Override
        public long toBytes(long sourceDataSize)
        {
            return sourceDataSize;
        }    
    },
    
    /** The kb. */
    KB {
        @Override
        public long toBytes(long sourceDataSize)
        {
            return sourceDataSize * KB_SIZE;
        }    
    },
    
    /** The mb. */
    MB {
        @Override
        public long toBytes(long sourceDataSize)
        {
            return sourceDataSize * MB_SIZE;
        }    
    },
    
    /** The gb. */
    GB {
        @Override
        public long toBytes(long sourceDataSize)
        {
            return sourceDataSize * GB_SIZE;
        }    
    };
    
    /** The Constant UNIT_SIZE. */
    private static final long UNIT_SIZE = 1024;
    
    /** The Constant KB_SIZE. */
    private static final long KB_SIZE = UNIT_SIZE;
    
    /** The Constant MB_SIZE. */
    private static final long MB_SIZE = KB_SIZE * UNIT_SIZE;
    
    /** The Constant GB_SIZE. */
    private static final long GB_SIZE = MB_SIZE * UNIT_SIZE;

    /**
     * To bytes.
     *
     * @param sourceDataSize the source data size
     * @return the long
     */
    public long toBytes(long sourceDataSize)
    {
        throw new AbstractMethodError();
    }

}
