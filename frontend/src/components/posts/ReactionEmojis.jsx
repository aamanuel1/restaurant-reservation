import React from 'react';
import { useGetReactionsByPostIdQuery, useAddReactionMutation } from '../../api/usersSlice';


export const ReactionButtons = ({ postId }) => {

  const { data: reactions, error, isLoading } = useGetReactionsByPostIdQuery(postId);

  const [addReaction] = useAddReactionMutation();
  const onReactionButtonClick = async(postId, reactionTypeId) => {
    try {
      await addReaction({ postId: postId, reactionTypeId: reactionTypeId}).unwrap();
    } catch (err) {
      console.error(`Failed to add reaction: ${err}`);
    }
  };


  const reactionButtons = reactions ? reactions.map((reaction) => {
      return (
        <button
          key={`${reaction.post.id}-${reaction.reactionType.id}`}
          type="button"
          className="muted-button reaction-button"
          onClick={() => onReactionButtonClick(reaction.post.id, reaction.reactionType.id)}
        >
          {reaction.reactionType.emojiIcon} {reaction.count}
        </button>
      )
    }
  ) : [];

  return <div>{reactionButtons}</div>
}
