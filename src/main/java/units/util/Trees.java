package units.util;

import lombok.val;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Zen.Liu
 * @apiNote
 * @since 2021-05-22
 */
public interface Trees {
    interface Builder<T> {
        boolean isBuilt();

        Builder<T> reset();

        Builder<T> build(BiFunction<List<T>, T, Integer> parentDecider);

        <R> Set<R> toTree(Function<T, R> ctor, BiConsumer<R, R> addChild);
    }

    static <T> Builder<T> builder(List<T> element) {
        return new TreeBuilder<>(element);
    }

    final class TreeBuilder<T> implements Builder<T> {
        static final class N {
            int p = -1;
            final int l;
            final Set<N> children = new HashSet<>();

            N(int l) {
                this.l = l;
            }

            N setP(int i) {
                p = i;
                return this;
            }
        }

        final List<T> elements;
        final List<N> nodes = new ArrayList<>();

        TreeBuilder(List<T> elements) {
            this.elements = elements;
        }

        @Override
        public TreeBuilder<T> reset() {
            nodes.clear();
            return this;
        }

        public boolean isBuilt() {
            return !nodes.isEmpty();
        }

        public synchronized TreeBuilder<T> build(BiFunction<List<T>, T, Integer> parentDecider) {
            val m = new ArrayList<N>();
            for (int i = 0; i < elements.size(); i++) {
                m.add(new N(i));
            }
            for (int i = 0; i < elements.size(); i++) {
                val r = parentDecider.apply(elements, elements.get(i));
                if (r == null || r < 0) continue;
                m.get(r).children.add(m.get(i).setP(r));
            }
            for (N n : m) {
                if (n.p == -1) {
                    nodes.add(n);
                }
            }
            return this;
        }

        public <R> Set<R> toTree(Function<T, R> ctor, BiConsumer<R, R> addChild) {
            if (nodes.isEmpty()) return Collections.emptySet();
            val roots = new HashSet<R>();
            val nQ = new ArrayDeque<N>();
            val rQ = new ArrayDeque<R>();
            for (N node : nodes) {
                val r = ctor.apply(elements.get(node.l));
                roots.add(r);
                nQ.add(node);
                rQ.add(r);
                while (!nQ.isEmpty()) {
                    val n = nQ.pop();
                    if (n.children.isEmpty()) {
                        rQ.pop();
                    } else {
                        val nr = rQ.pop();
                        for (N child : n.children) {
                            val cR = ctor.apply(elements.get(child.l));
                            addChild.accept(nr, cR);
                            nQ.push(child);
                            rQ.push(cR);
                        }
                    }
                }
            }
            return roots;
        }
    }
/*
    final static   List<Entry<Integer, String>> ele = Arrays.<Entry<Integer, String>>asList(
        //region Data
        entryOf(130000, "河北省"),
        entryOf(130100, "石家庄市"),
        entryOf(130102, "长安区"),
        entryOf(130104, "桥西区"),
        entryOf(130105, "新华区"),
        entryOf(130107, "井陉矿区"),
        entryOf(130108, "裕华区"),
        entryOf(130109, "藁城区"),
        entryOf(130110, "鹿泉区"),
        entryOf(130111, "栾城区"),
        entryOf(130121, "井陉县"),
        entryOf(130123, "正定县"),
        entryOf(130125, "行唐县"),
        entryOf(130126, "灵寿县"),
        entryOf(130127, "高邑县"),
        entryOf(130128, "深泽县"),
        entryOf(130129, "赞皇县"),
        entryOf(130130, "无极县"),
        entryOf(130131, "平山县"),
        entryOf(130132, "元氏县"),
        entryOf(130133, "赵县"),
        entryOf(130181, "辛集市"),
        entryOf(130183, "晋州市"),
        entryOf(130184, "新乐市"),
        entryOf(130200, "唐山市"),
        entryOf(130202, "路南区"),
        entryOf(130203, "路北区"),
        entryOf(130204, "古冶区"),
        entryOf(130205, "开平区"),
        entryOf(130207, "丰南区"),
        entryOf(130208, "丰润区"),
        entryOf(130209, "曹妃甸区"),
        entryOf(130223, "滦县"),
        entryOf(130224, "滦南县"),
        entryOf(130225, "乐亭县"),
        entryOf(130227, "迁西县"),
        entryOf(130229, "玉田县"),
        entryOf(130281, "遵化市"),
        entryOf(130283, "迁安市"),
        entryOf(130300, "秦皇岛市"),
        entryOf(130302, "海港区"),
        entryOf(130303, "山海关区"),
        entryOf(130304, "北戴河区"),
        entryOf(130306, "抚宁区"),
        entryOf(130321, "青龙满族自治县"),
        entryOf(130322, "昌黎县"),
        entryOf(130324, "卢龙县"),
        entryOf(130400, "邯郸市"),
        entryOf(130402, "邯山区"),
        entryOf(130403, "丛台区"),
        entryOf(130404, "复兴区"),
        entryOf(130406, "峰峰矿区"),
        entryOf(130421, "邯郸县"),
        entryOf(130423, "临漳县"),
        entryOf(130424, "成安县"),
        entryOf(130425, "大名县"),
        entryOf(130426, "涉县"),
        entryOf(130427, "磁县"),
        entryOf(130428, "肥乡县"),
        entryOf(130429, "永年县"),
        entryOf(130430, "邱县"),
        entryOf(130431, "鸡泽县"),
        entryOf(130432, "广平县"),
        entryOf(130433, "馆陶县"),
        entryOf(130434, "魏县"),
        entryOf(130435, "曲周县"),
        entryOf(130481, "武安市"),
        entryOf(130500, "邢台市"),
        entryOf(130502, "桥东区"),
        entryOf(130503, "桥西区"),
        entryOf(130521, "邢台县"),
        entryOf(130522, "临城县"),
        entryOf(130523, "内丘县"),
        entryOf(130524, "柏乡县"),
        entryOf(130525, "隆尧县"),
        entryOf(130526, "任县"),
        entryOf(130527, "南和县"),
        entryOf(130528, "宁晋县"),
        entryOf(130529, "巨鹿县"),
        entryOf(130530, "新河县"),
        entryOf(130531, "广宗县"),
        entryOf(130532, "平乡县"),
        entryOf(130533, "威县"),
        entryOf(130534, "清河县"),
        entryOf(130535, "临西县"),
        entryOf(130581, "南宫市"),
        entryOf(130582, "沙河市"),
        entryOf(130600, "保定市"),
        entryOf(130602, "竞秀区"),
        entryOf(130604, "南市区"),
        entryOf(130606, "莲池区"),
        entryOf(130607, "满城区"),
        entryOf(130608, "清苑区"),
        entryOf(130609, "徐水区"),
        entryOf(130623, "涞水县"),
        entryOf(130624, "阜平县"),
        entryOf(130626, "定兴县"),
        entryOf(130627, "唐县"),
        entryOf(130628, "高阳县"),
        entryOf(130629, "容城县"),
        entryOf(130630, "涞源县"),
        entryOf(130631, "望都县"),
        entryOf(130632, "安新县"),
        entryOf(130633, "易县"),
        entryOf(130634, "曲阳县"),
        entryOf(130635, "蠡县"),
        entryOf(130636, "顺平县"),
        entryOf(130637, "博野县"),
        entryOf(130638, "雄县"),
        entryOf(130681, "涿州市"),
        entryOf(130682, "定州市"),
        entryOf(130683, "安国市"),
        entryOf(130684, "高碑店市"),
        entryOf(130700, "张家口市"),
        entryOf(130702, "桥东区"),
        entryOf(130703, "桥西区"),
        entryOf(130705, "宣化区"),
        entryOf(130706, "下花园区"),
        entryOf(130721, "宣化县"),
        entryOf(130722, "张北县"),
        entryOf(130723, "康保县"),
        entryOf(130724, "沽源县"),
        entryOf(130725, "尚义县"),
        entryOf(130726, "蔚县"),
        entryOf(130727, "阳原县"),
        entryOf(130728, "怀安县"),
        entryOf(130729, "万全县"),
        entryOf(130730, "怀来县"),
        entryOf(130731, "涿鹿县"),
        entryOf(130732, "赤城县"),
        entryOf(130733, "崇礼县"),
        entryOf(130800, "承德市"),
        entryOf(130802, "双桥区"),
        entryOf(130803, "双滦区"),
        entryOf(130804, "鹰手营子矿区"),
        entryOf(130821, "承德县"),
        entryOf(130822, "兴隆县"),
        entryOf(130823, "平泉县"),
        entryOf(130824, "滦平县"),
        entryOf(130825, "隆化县"),
        entryOf(130826, "丰宁满族自治县"),
        entryOf(130827, "宽城满族自治县"),
        entryOf(130828, "围场满族蒙古族自治县"),
        entryOf(130900, "沧州市"),
        entryOf(130902, "新华区"),
        entryOf(130903, "运河区"),
        entryOf(130921, "沧县"),
        entryOf(130922, "青县"),
        entryOf(130923, "东光县"),
        entryOf(130924, "海兴县"),
        entryOf(130925, "盐山县"),
        entryOf(130926, "肃宁县"),
        entryOf(130927, "南皮县"),
        entryOf(130928, "吴桥县"),
        entryOf(130929, "献县"),
        entryOf(130930, "孟村回族自治县"),
        entryOf(130981, "泊头市"),
        entryOf(130982, "任丘市"),
        entryOf(130983, "黄骅市"),
        entryOf(130984, "河间市"),
        entryOf(131000, "廊坊市"),
        entryOf(131002, "安次区"),
        entryOf(131003, "广阳区"),
        entryOf(131022, "固安县"),
        entryOf(131023, "永清县"),
        entryOf(131024, "香河县"),
        entryOf(131025, "大城县"),
        entryOf(131026, "文安县"),
        entryOf(131028, "大厂回族自治县"),
        entryOf(131081, "霸州市"),
        entryOf(131082, "三河市"),
        entryOf(131100, "衡水市"),
        entryOf(131102, "桃城区"),
        entryOf(131121, "枣强县"),
        entryOf(131122, "武邑县"),
        entryOf(131123, "武强县"),
        entryOf(131124, "饶阳县"),
        entryOf(131125, "安平县"),
        entryOf(131126, "故城县"),
        entryOf(131127, "景县"),
        entryOf(131128, "阜城县"),
        entryOf(131181, "冀州市"),
        entryOf(131182, "深州市")
        //endregion
    );
    @lombok.Builder @ToString
    class P<T>{
        final T k;
        final List<T> c=new ArrayList<>();
    }
    public static void main(String[] args) {

        val builder=builder(ele);
        val s=System.currentTimeMillis();
        for (int j = 0; j < 100; j++) {

            val r=    builder.reset()
                .build((a,b)->{
                val l=(int)b.getKey();
                val isCity=l%100==0;
                val isPro=l%10000==0;
                val l2=l/100+"" ;
                if(isPro) return null;
                for(int i=0; i<a.size();i++){
                    val e=a.get(i);
                    val code=(int)e.getKey();
                    if(code==l) continue;
                    if(isCity &&  code%10000==0){
                        return i;
                    }else if(Integer.toString(code).startsWith(l2)){
                        return i;
                    }
                }
                return null;
            })
                .toTree(x->P.builder().k(x).build(),
                    (x,t)->x.c.add(t));
            System.out.println(r);
        }
        System.out.println((System.currentTimeMillis()-s));
    }*/
}
