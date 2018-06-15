package lt.dualpair.android.data.mapper;

import lt.dualpair.android.data.local.entity.Match;
import lt.dualpair.android.data.local.entity.Swipe;

public class MatchResourceMapper {

    private Long userPrincipalId;
    private UserResourceMapper userResourceMapper;

    public MatchResourceMapper(Long userPrincipalId, UserResourceMapper userResourceMapper) {
        this.userPrincipalId = userPrincipalId;
        this.userResourceMapper = userResourceMapper;
    }

    public Result map(lt.dualpair.android.data.remote.resource.Match matchResource) {
        Swipe swipe = new Swipe();
        swipe.setId(matchResource.getUser().getId());
        swipe.setUserId(userPrincipalId);
        Long opponentUserId = matchResource.getOpponent().getUser().getId();
        swipe.setWho(opponentUserId);
        swipe.setType(matchResource.getUser().getResponse().toString());
        lt.dualpair.android.data.local.entity.Match match = null;
        if (matchResource.isMutual()) {
            match = new lt.dualpair.android.data.local.entity.Match();
            match.setId(matchResource.getId());
            match.setOpponentId(opponentUserId);
        }
        UserResourceMapper.Result userMappingResult = userResourceMapper.map(matchResource.getOpponent().getUser());
        return new Result(swipe, match, userMappingResult);
    }

    public static class Result {
        Swipe swipe;
        Match match;
        UserResourceMapper.Result userMappingResult;

        public Result(Swipe swipe, Match match, UserResourceMapper.Result userMappingResult) {
            this.swipe = swipe;
            this.match = match;
            this.userMappingResult = userMappingResult;
        }

        public Swipe getSwipe() {
            return swipe;
        }

        public Match getMatch() {
            return match;
        }

        public UserResourceMapper.Result getUserMappingResult() {
            return userMappingResult;
        }
    }

}
