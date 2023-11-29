import React from "react";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";


const baseUrl = "http://localhost:8080/api/v1";

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: fetchBaseQuery({baseUrl: baseUrl}),
    endpoints: (builder) => ({
        getAllUsers: builder.query({
            query: () => "users/all-users",
        }),
        getUserById: builder.query({
            query: (userId) => `users/by-id/${userId}`,
        }),
        getUserByUserName: builder.query({
            query: (userName) => `users/by-username/${userName}`,
        }),
        addNewUser: builder.mutation({
            query: (newUser) => ({
                url: 'users/add-user',
                method: 'POST',
                body: newUser,
            }),
        }),
        getAllPosts: builder.query({
            query: () => "user-posts/all-posts",
        }),
        getPostByIdAndUserId: builder.query({
            query: ({postId, userId}) => `user-posts/${postId}/${userId}`,
        }),
        getUserPosts: builder.query({
            query: (userId) => `user-posts/by/${userId}`,
        }),
        getPostById: builder.query({
            query: (postId) => `/posts/${postId}`,
        }),
        addNewPost: builder.mutation({
            query: ({post, userId}) => ({
                url: `posts/add-post/${userId}`,
                method: 'POST',
                body: post,
            }),
        }),
        editPost: builder.mutation({
            query: ({post}) => ({
                url: `posts/edit-post`,
                method: 'PUT',
                body: post,
            }),
            // invalidatesTags: (result, error, { postId }) => [{ type: 'Reaction', id: postId}],
        }),
        getReactionsByPostId: builder.query({
            query: (postId) => `/reactions/post-reactions/${postId}`,
            providesTags: (result, error, postId) => [{ type: 'Reaction', id: postId }],
        }),
        addReaction: builder.mutation({
            query: ({ postId, reactionTypeId}) => ({
                url: `reactions/add/${postId}/${reactionTypeId}`,
                method: 'POST',
            }),
            invalidatesTags: (result, error, { postId }) => [{ type: 'Reaction', id: postId}],
        }),
    }),
})

export const { 
    useGetAllUsersQuery,
    useGetUserByIdQuery,
    useGetUserByUserNameQuery,
    useAddNewUserMutation,
    useGetUserPostsQuery,
    useGetAllPostsQuery,
    useGetPostByIdQuery,
    useAddNewPostMutation,
    useGetPostByIdAndUserIdQuery,
    useGetReactionsByPostIdQuery,
    useAddReactionMutation,
    useEditPostMutation,
} = usersApi;

