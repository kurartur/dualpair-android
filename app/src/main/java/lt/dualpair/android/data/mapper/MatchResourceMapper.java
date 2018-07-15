package lt.dualpair.android.data.mapper;

import java.util.Date;

import lt.dualpair.android.data.local.entity.Match;

public class MatchResourceMapper {

    private UserResourceMapper userResourceMapper;

    public MatchResourceMapper(UserResourceMapper userResourceMapper) {
        this.userResourceMapper = userResourceMapper;
    }

    public Result map(lt.dualpair.android.data.remote.resource.Match matchResource) {
        lt.dualpair.android.data.local.entity.Match match = new lt.dualpair.android.data.local.entity.Match();
        match.setOpponentId(matchResource.getUser().getId());
        match.setDate(new Date());
        match.setName(matchResource.getUser().getName());
        match.setPhotoSource(matchResource.getUser().getPhotos().get(0).getSourceUrl());
        UserResourceMapper.Result userMappingResult = userResourceMapper.map(matchResource.getUser());
        return new Result(match, userMappingResult);
    }

    public static class Result {
        Match match;
        UserResourceMapper.Result userMappingResult;

        public Result(Match match, UserResourceMapper.Result userMappingResult) {
            this.match = match;
            this.userMappingResult = userMappingResult;
        }

        public Match getMatch() {
            return match;
        }

        public UserResourceMapper.Result getUserMappingResult() {
            return userMappingResult;
        }
    }

}
