package com.antwerkz.wsdemos.war;

public enum Type {
    AA {
        @Override
        public int[] dimensions() {
            return new int[] {1, 2};
        }
    },
    TANK {
        @Override
        public int[] dimensions() {
            return new int[] {2, 2};
        }
    },
    PLATOON{
        @Override
        public int[] dimensions() {
            return new int[] {2, 4};
        }

    },
    HQ{
        @Override
        public int[] dimensions() {
            return new int[] {3, 3};
        }

    };

    boolean[][] create(int width, int height) {
        boolean spaces[][] = new boolean[width][];
        for (int i = 0; i < spaces.length; i++) {
            spaces[i] = new boolean[height];
        }
        
        return spaces;
    }

    public void createHits(final Piece piece) {
        final int[] dimensions = dimensions();
        piece.setHits(create(dimensions[0], dimensions[1]));
    }

    public abstract int[] dimensions();

    @Override
    public String toString() {
        return name();
    }
}