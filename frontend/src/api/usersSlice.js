import React from "react";
import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";


const baseUrl = "http://localhost:8080/api";

export const usersApi = createApi({
    reducerPath: 'usersApi',
    baseQuery: fetchBaseQuery({baseUrl: baseUrl}),
    endpoints: (builder) => ({
        getAllCustomers: builder.query({
            query: () => "customer/list",
        }),
        getCustomerByEmail: builder.query({
            query: (email) => `customer/profile/${email}`,
        }),
        getCustomerById: builder.query({
            query: (id) => `customer/profile/id/${id}`,
        }),
        addNewCustomer: builder.mutation({
            query: (newCustomer) => ({
                url: 'customer/add',
                method: 'POST',
                body: newCustomer,
            }),
        }),
        getAllFoods: builder.query({
            query: () => "food/list",
        }),
        getAllRestaurants: builder.query({
            query: () => "restaurant/list",
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
    useGetAllCustomersQuery,
    useGetCustomerByEmailQuery,
    useGetCustomerByIdQuery,
    useAddNewCustomerMutation,
    useGetAllFoodsQuery,
    useGetAllRestaurantsQuery,

    useGetUserPostsQuery,
    useGetPostByIdQuery,
    useAddNewPostMutation,
    useGetPostByIdAndUserIdQuery,
    useGetReactionsByPostIdQuery,
    useAddReactionMutation,
    useEditPostMutation,
} = usersApi;

