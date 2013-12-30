package reedey.server.tracking;

public class Track17Hash {

    //int l = 0;
    //String R = "";

    String yqtrackapi_i(String s) {
        return yqtrackapi_v(yqtrackapi_r(yqtrackapi_z(s)));
    };

    String yqtrackapi_d(String s) {
        return yqtrackapi_t(yqtrackapi_r(yqtrackapi_z(s)));
    };

    String yqtrackapi_h(String C, String d) {
        return yqtrackapi_v(yqtrackapi_q(yqtrackapi_z(C), yqtrackapi_z(d)));
    };

    String yqtrackapi_c(String C, String d) {
        return yqtrackapi_t(yqtrackapi_q(yqtrackapi_z(C), yqtrackapi_z(d)));
    };

    boolean yqtrackapi_p() {
        return yqtrackapi_i(new String(new char[]{0x61,0x62,0x63})).toLowerCase().equals(
                new String(new char[]{0x39,0x30,0x30,0x31,0x35,0x30,0x39,0x38,0x33,0x63,0x64,
                        0x32,0x34,0x66,0x62,0x30,0x64,0x36,0x39,0x36,0x33,0x66,0x37,0x64,0x32,
                        0x38,0x65,0x31,0x37,0x66,0x37,0x32}));
    };

    String yqtrackapi_r(String s) {
        return yqtrackapi_f(yqtrackapi_e(yqtrackapi_u(s), s.length() * 8));
    };

    String yqtrackapi_q(String key, String data) {
        int[] G = yqtrackapi_u(key);
        if (G.length > 16) G = yqtrackapi_e(G, key.length() * 8);
        int[] ah = new int[16],
            ab = new int[16];
        for (int i = 0; i < 16; i++) {
            ah[i] = G[i] ^ 0x36363636;
            ab[i] = G[i] ^ 0x5C5C5C5C;
        };
        int[] ah2 = yqtrackapi_u(data);
        int[] ah3 = new int[ah.length + ah2.length];
        System.arraycopy(ah, 0, ah3, 0, ah.length);
        System.arraycopy(ah2, 0, ah3, ah.length, ah2.length);
        
        
        int[] hash = yqtrackapi_e(ah3, 512 + data.length() * 8);
        int[] ab2 = new int[ab.length + hash.length];
        System.arraycopy(ab, 0, ab2, 0, ab.length);
        System.arraycopy(hash, 0, ab2, ab.length, hash.length);
        return yqtrackapi_f(yqtrackapi_e(ab2, 512 + 128));
    };

    String yqtrackapi_v(String input) {
        String v = /*l > 0 ?*/ new String(new char[]{0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x41,0x42,0x43,0x44,0x45,0x46})
                ;//: new String(new char[]{0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x61,0x62,0x63,0x64,0x65,0x66});
        String output = "";
        int x;
        for (int i = 0; i < input.length(); i++) {
            x = input.charAt(i);
            output += new String(new char[]{v.charAt((x >>> 4) & 0x0F),v.charAt(x & 0x0F)});
        };
        return output;
    };

    String yqtrackapi_t(String input) {
        String tab = new String(new char[]{0x41,0x42,0x43,0x44,0x45,0x46,0x47,0x48,0x49,0x4a,
                0x4b,0x4c,0x4d,0x4e,0x4f,0x50,0x51,0x52,0x53,0x54,0x55,0x56,0x57,0x58,0x59,
                0x5a,0x61,0x62,0x63,0x64,0x65,0x66,0x67,0x68,0x69,0x6a,0x6b,0x6c,0x6d,0x6e,
                0x6f,0x70,0x71,0x72,0x73,0x74,0x75,0x76,0x77,0x78,0x79,0x7a,0x30,0x31,0x32,
                0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x2b,0x2f});
        String output = "";
        int H = input.length();
        for (int i = 0; i < H; i += 3) {
            int bH = (input.charAt(i) << 16) | (i + 1 < H ? input.charAt(i + 1) << 8 : 0) | (i + 2 < H ? input.charAt(i + 2) : 0);
            for (int O = 0; O < 4; O++) {
                if (i * 8 + O * 6 > input.length() * 8) output += "";//R;
                else output += tab.charAt((bH >>> 6 * (3 - O)) & 0x3F);
            }
        };
        return output;
    };

    String yqtrackapi_z(String input) {
        String output = "";
        int i = -1;
        int x, y;
        while (++i < input.length()) {
            x = input.charAt(i);
            y = i + 1 < input.length() ? input.charAt(i + 1) : 0;
            if (0xD800 <= x && x <= 0xDBFF && 0xDC00 <= y && y <= 0xDFFF) {
                x = 0x10000 + ((x & 0x03FF) << 10) + (y & 0x03FF);
                i++;
            };
            if (x <= 0x7F) output += new String(new char[]{
                    (char)(x)});
            else if (x <= 0x7FF) output += new String(new char[]{
                    (char)(0xC0 | ((x >>> 6) & 0x1F)), 
                    (char)(0x80 | (x & 0x3F))});
            else if (x <= 0xFFFF) output += new String(new char[]{
                    (char)(0xE0 | ((x >>> 12) & 0x0F)), 
                    (char)(0x80 | ((x >>> 6) & 0x3F)), 
                    (char)(0x80 | (x & 0x3F))});
            else if (x <= 0x1FFFFF) output += new String(new char[]{
                    (char)(0xF0 | ((x >>> 18) & 0x07)), 
                    (char)(0x80 | ((x >>> 12) & 0x3F)), 
                    (char)(0x80 | ((x >>> 6) & 0x3F)), 
                    (char)(0x80 | (x & 0x3F))});
        };
        return output;
    };

    int[] yqtrackapi_u(String input) {
        int[] output = new int[(input.length() >> 2) + 6];
        for (int i = 0; i < output.length; i++) output[i] = 0;
        for (int i = 0; i < input.length() * 8; i += 8) output[i >> 5] |= (input.charAt(i / 8) & 0xFF) << (i % 32);
        return output;
    };

    String yqtrackapi_f(int[] input) {
        String output = "";
        for (int i = 0; i < input.length * 32; i += 8) 
            output += Character.valueOf((char) ((input[i >> 5] >>> (i % 32)) & 0xFF));
        return output;
    };

    int[] yqtrackapi_e(int[] x, int H) {
        x[H >> 5] |= 0x80 << ((H) % 32);
        x[(((H + 64) >>> 9) << 4) + 14] = H;
        int a = 1732584193;
        int b = -271733879;
        int c = -1732584194;
        int d = 271733878;
        for (int i = 0; i < x.length; i += 16) {
            int bO = a;
            int bx = b;
            int bu = c;
            int az = d;
            a = yqtrackapi_l(a, b, c, d, x[i + 0], 7, -680876936);
            d = yqtrackapi_l(d, a, b, c, x[i + 1], 12, -389564586);
            c = yqtrackapi_l(c, d, a, b, x[i + 2], 17, 606105819);
            b = yqtrackapi_l(b, c, d, a, x[i + 3], 22, -1044525330);
            a = yqtrackapi_l(a, b, c, d, x[i + 4], 7, -176418897);
            d = yqtrackapi_l(d, a, b, c, x[i + 5], 12, 1200080426);
            c = yqtrackapi_l(c, d, a, b, x[i + 6], 17, -1473231341);
            b = yqtrackapi_l(b, c, d, a, x[i + 7], 22, -45705983);
            a = yqtrackapi_l(a, b, c, d, x[i + 8], 7, 1770035416);
            d = yqtrackapi_l(d, a, b, c, x[i + 9], 12, -1958414417);
            c = yqtrackapi_l(c, d, a, b, x[i + 10], 17, -42063);
            b = yqtrackapi_l(b, c, d, a, x[i + 11], 22, -1990404162);
            a = yqtrackapi_l(a, b, c, d, x[i + 12], 7, 1804603682);
            d = yqtrackapi_l(d, a, b, c, x[i + 13], 12, -40341101);
            c = yqtrackapi_l(c, d, a, b, x[i + 14], 17, -1502002290);
            b = yqtrackapi_l(b, c, d, a, x[i + 15], 22, 1236535329);
            a = yqtrackapi_m(a, b, c, d, x[i + 1], 5, -165796510);
            d = yqtrackapi_m(d, a, b, c, x[i + 6], 9, -1069501632);
            c = yqtrackapi_m(c, d, a, b, x[i + 11], 14, 643717713);
            b = yqtrackapi_m(b, c, d, a, x[i + 0], 20, -373897302);
            a = yqtrackapi_m(a, b, c, d, x[i + 5], 5, -701558691);
            d = yqtrackapi_m(d, a, b, c, x[i + 10], 9, 38016083);
            c = yqtrackapi_m(c, d, a, b, x[i + 15], 14, -660478335);
            b = yqtrackapi_m(b, c, d, a, x[i + 4], 20, -405537848);
            a = yqtrackapi_m(a, b, c, d, x[i + 9], 5, 568446438);
            d = yqtrackapi_m(d, a, b, c, x[i + 14], 9, -1019803690);
            c = yqtrackapi_m(c, d, a, b, x[i + 3], 14, -187363961);
            b = yqtrackapi_m(b, c, d, a, x[i + 8], 20, 1163531501);
            a = yqtrackapi_m(a, b, c, d, x[i + 13], 5, -1444681467);
            d = yqtrackapi_m(d, a, b, c, x[i + 2], 9, -51403784);
            c = yqtrackapi_m(c, d, a, b, x[i + 7], 14, 1735328473);
            b = yqtrackapi_m(b, c, d, a, x[i + 12], 20, -1926607734);
            a = yqtrackapi_n(a, b, c, d, x[i + 5], 4, -378558);
            d = yqtrackapi_n(d, a, b, c, x[i + 8], 11, -2022574463);
            c = yqtrackapi_n(c, d, a, b, x[i + 11], 16, 1839030562);
            b = yqtrackapi_n(b, c, d, a, x[i + 14], 23, -35309556);
            a = yqtrackapi_n(a, b, c, d, x[i + 1], 4, -1530992060);
            d = yqtrackapi_n(d, a, b, c, x[i + 4], 11, 1272893353);
            c = yqtrackapi_n(c, d, a, b, x[i + 7], 16, -155497632);
            b = yqtrackapi_n(b, c, d, a, x[i + 10], 23, -1094730640);
            a = yqtrackapi_n(a, b, c, d, x[i + 13], 4, 681279174);
            d = yqtrackapi_n(d, a, b, c, x[i + 0], 11, -358537222);
            c = yqtrackapi_n(c, d, a, b, x[i + 3], 16, -722521979);
            b = yqtrackapi_n(b, c, d, a, x[i + 6], 23, 76029189);
            a = yqtrackapi_n(a, b, c, d, x[i + 9], 4, -640364487);
            d = yqtrackapi_n(d, a, b, c, x[i + 12], 11, -421815835);
            c = yqtrackapi_n(c, d, a, b, x[i + 15], 16, 530742520);
            b = yqtrackapi_n(b, c, d, a, x[i + 2], 23, -995338651);
            a = yqtrackapi_o(a, b, c, d, x[i + 0], 6, -198630844);
            d = yqtrackapi_o(d, a, b, c, x[i + 7], 10, 1126891415);
            c = yqtrackapi_o(c, d, a, b, x[i + 14], 15, -1416354905);
            b = yqtrackapi_o(b, c, d, a, x[i + 5], 21, -57434055);
            a = yqtrackapi_o(a, b, c, d, x[i + 12], 6, 1700485571);
            d = yqtrackapi_o(d, a, b, c, x[i + 3], 10, -1894986606);
            c = yqtrackapi_o(c, d, a, b, x[i + 10], 15, -1051523);
            b = yqtrackapi_o(b, c, d, a, x[i + 1], 21, -2054922799);
            a = yqtrackapi_o(a, b, c, d, x[i + 8], 6, 1873313359);
            d = yqtrackapi_o(d, a, b, c, x[i + 15], 10, -30611744);
            c = yqtrackapi_o(c, d, a, b, x[i + 6], 15, -1560198380);
            b = yqtrackapi_o(b, c, d, a, x[i + 13], 21, 1309151649);
            a = yqtrackapi_o(a, b, c, d, x[i + 4], 6, -145523070);
            d = yqtrackapi_o(d, a, b, c, x[i + 11], 10, -1120210379);
            c = yqtrackapi_o(c, d, a, b, x[i + 2], 15, 718787259);
            b = yqtrackapi_o(b, c, d, a, x[i + 9], 21, -343485551);
            a = yqtrackapi_w(a, bO);
            b = yqtrackapi_w(b, bx);
            c = yqtrackapi_w(c, bu);
            d = yqtrackapi_w(d, az);
        };
        return new int[]{a, b, c, d};
    };

    int yqtrackapi_k(int q, int a, int b, int x, int s, int T) {
        return yqtrackapi_w(yqtrackapi_g(yqtrackapi_w(yqtrackapi_w(a, q), yqtrackapi_w(x, T)), s), b);
    };

    int yqtrackapi_l(int a, int b, int c, int d, int x, int s, int T) {
        return yqtrackapi_k((b & c) | ((~b) & d), a, b, x, s, T);
    };

    int yqtrackapi_m(int a, int b, int c, int d, int x, int s, int T) {
        return yqtrackapi_k((b & d) | (c & (~d)), a, b, x, s, T);
    };

    int yqtrackapi_n(int a, int b, int c, int d, int x, int s, int T) {
        return yqtrackapi_k(b ^ c ^ d, a, b, x, s, T);
    };

    int yqtrackapi_o(int a, int b, int c, int d, int x, int s, int T) {
        return yqtrackapi_k(c ^ (b | (~d)), a, b, x, s, T);
    };

    int yqtrackapi_w(int x, int y) {
        int ai = (x & 0xFFFF) + (y & 0xFFFF);
        int aw = (x >> 16) + (y >> 16) + (ai >> 16);
        return (aw << 16) | (ai & 0xFFFF);
    };

    int yqtrackapi_g(int aV, int bs) {
        return (aV << bs) | (aV >>> (32 - bs));
    };

    String yqtrackapi_j(String[] params) {
            if (params.length == 0) return null;
            StringBuilder str = new StringBuilder();
            String delimeter = new String(new char[]{0x7b,0x45,0x44,0x46,0x43,0x45,0x39,0x38,0x42,0x2d,0x31,0x43,0x45,0x36,0x2d,0x34,0x44,0x38,0x37,0x2d,0x38,0x43,0x34,0x41,0x2d,0x38,0x37,0x30,0x44,0x31,0x34,0x30,0x42,0x36,0x32,0x42,0x41,0x7d});
            for (int i = 0; i < params.length; i++)
                str.append(params[i]).append(delimeter);
            str.append("www.17track.net");
            return yqtrackapi_i(str.toString());

    };

    String hs(String barcode) {
        return yqtrackapi_j(new String[]{barcode, "0"});
    }
    
    public static void main(String[] args) {
        System.out.println(new Track17Hash().hs("RC336753543CN"));
    }

}
