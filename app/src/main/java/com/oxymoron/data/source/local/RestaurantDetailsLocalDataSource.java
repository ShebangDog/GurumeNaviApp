package com.oxymoron.data.source.local;

import androidx.annotation.NonNull;

import com.oxymoron.data.RestaurantDetail;
import com.oxymoron.data.source.RestaurantDetailsDataSource;
import com.oxymoron.data.source.local.data.RestaurantId;
import com.oxymoron.data.source.remote.api.PageState;
import com.oxymoron.data.source.remote.api.serializable.LocationInformation;
import com.oxymoron.data.source.remote.api.serializable.Range;
import com.oxymoron.util.multi.AppExecutors;

import java.util.List;

public class RestaurantDetailsLocalDataSource implements RestaurantDetailsDataSource {
    private static volatile RestaurantDetailsLocalDataSource ourInstance;

    private final AppExecutors appExecutors;

    private final RestaurantDetailsDao restaurantDetailsDao;

    public static RestaurantDetailsLocalDataSource getInstance(AppExecutors appExecutors,
                                                               RestaurantDetailsDao restaurantDetailsDao) {
        if (ourInstance == null) {
            synchronized (RestaurantDetailsLocalDataSource.class) {
                if (ourInstance == null) {
                    ourInstance = new RestaurantDetailsLocalDataSource(appExecutors, restaurantDetailsDao);
                }
            }
        }

        return ourInstance;
    }

    private RestaurantDetailsLocalDataSource(AppExecutors appExecutors, RestaurantDetailsDao restaurantDetailsDao) {
        this.appExecutors = appExecutors;
        this.restaurantDetailsDao = restaurantDetailsDao;
    }

    @Override
    public void getRestaurantDetails(@NonNull LoadRestaurantDetailsCallback callback) {
        Runnable runnable = () -> {
            List<RestaurantDetail> restaurantDetailList =
                    this.restaurantDetailsDao.getRestaurantDetails();

            this.appExecutors.mainThread().execute(() -> {
                if (restaurantDetailList.isEmpty()) {
                    System.out.println("is empty");
                    callback.onDataNotAvailable();
                } else {
                    System.out.println("data loaded");
                    callback.onRestaurantDetailsLoaded(restaurantDetailList);
                }
            });
        };

        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRestaurantDetails(@NonNull List<RestaurantId> restaurantIdList, @NonNull LoadRestaurantDetailsCallback callback) {
        Runnable runnable = () -> {
            List<RestaurantDetail> restaurantDetailList =
                    this.restaurantDetailsDao.getRestaurantDetails(restaurantIdList);

            this.appExecutors.mainThread().execute(() -> {
                if (restaurantDetailList == null || restaurantDetailList.size() != restaurantIdList.size()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onRestaurantDetailsLoaded(restaurantDetailList);
                }
            });
        };

        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void getRestaurantDetails(@NonNull Range range, @NonNull LocationInformation locationInformation,
                                     @NonNull GetRestaurantSearchResultCallback callback) {

        callback.onDataNotAvailable();
    }

    @Override
    public void getRestaurantDetails(@NonNull Range range, @NonNull LocationInformation locationInformation,
                                     @NonNull PageState pageState, @NonNull GetRestaurantSearchResultCallback callback) {

        callback.onDataNotAvailable();
    }

    @Override
    public void getRestaurantDetail(@NonNull RestaurantId id, @NonNull GetRestaurantDetailsCallback callback) {
        Runnable runnable = () -> {
            RestaurantDetail restaurantDetail = this.restaurantDetailsDao.getRestaurantDetail(id);

            this.appExecutors.mainThread().execute(() -> {
                if (restaurantDetail == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onRestaurantDetailLoaded(restaurantDetail);
                }
            });
        };

        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void saveRestaurantDetail(@NonNull RestaurantDetail restaurantDetail) {
        Runnable runnable = () -> this.restaurantDetailsDao.insertRestaurantDetail(restaurantDetail);

        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteRestaurantDetail(@NonNull RestaurantId id) {
        Runnable runnable = () -> this.restaurantDetailsDao.deleteRestaurantDetail(id);

        this.appExecutors.diskIO().execute(runnable);
    }

    @Override
    public void deleteAllRestaurantDetail() {
        Runnable runnable = this.restaurantDetailsDao::deleteAllRestaurantDetail;

        this.appExecutors.diskIO().execute(runnable);
    }
}
