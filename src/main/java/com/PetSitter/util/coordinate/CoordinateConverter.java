package com.PetSitter.util.coordinate;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.opengis.referencing.operation.MathTransform;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.referencing.operation.TransformException;


/**
 * 좌표계 변환 유틸 클래스(지도에서 마커 찍기 위해서, 네이버 지도나 카카오 지도 API는 위경도(WGS84, EPSG:4326) 좌표를 사용해서 좌표 변환이 필수)
 * EPSG:5174 좌표계 -> WGS84 좌표계(EPSG:4326)로 변환
 */
public class CoordinateConverter {

    private static final CoordinateReferenceSystem sourceCRS;
    private static final CoordinateReferenceSystem targetCRS; // 위경도

    static {
        try {
            sourceCRS = CRS.decode("EPSG:5174");
            targetCRS = CRS.decode("EPSG:4326");
        } catch (Exception e) {
            throw new RuntimeException("좌표계 초기화 실패.", e);
        }
    }

    /**
     * @param tmX: TM 좌표계 기준 X 좌표 (동쪽 방향 거리)
     * @param tmY: TM 좌표계 기준 Y 좌표 (북쪽 방향 거리)
     */
    public static LatLng convertTMToWGS84(Double tmX, Double tmY) {
        MathTransform transform = null;
        try {
            // 원본 -> 대상 좌표계를 변환할 수 있는 MathTransform 객체를 갖고 옴.(lenient: false로 주면 엄격한 비교로 변환하는데 이러면 축의 방향, 단위, 차원 수 등이 아예 맞아야 해서 보통 true로 관대한 비교로 변환함.)
            transform = CRS.findMathTransform(sourceCRS, targetCRS, true);

            // 원본 좌표(tmX, tmY)를 DirectPosition2D(src)에 담고 변환한 좌표를 dest에 저장
            DirectPosition2D src = new DirectPosition2D(sourceCRS, tmX, tmY);
            DirectPosition2D dest = new DirectPosition2D();
            transform.transform(src, dest); // 좌표 변환

            return new LatLng(dest.getX(), dest.getY()); // 위경도 좌표 recode 객체 클래스로 반환
        } catch (FactoryException | TransformException e) {
            throw new RuntimeException("좌표 변환에 실패했습니다.", e);
        }
    }
}
