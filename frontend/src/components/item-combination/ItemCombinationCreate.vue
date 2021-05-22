v<template>
   <div class="item-combination-items-association">

     <v-row
       v-for="(item, index) in sQuestionDetails.items"
       :key="index"
       data-cy="questionItemsInput"
     >
       <v-col cols="10" offset="1">
         <v-textarea
           v-model="item.content"
           :label="`Item ${index + 1}`"
           :data-cy="`Item${index + 1}`"
           rows="1"
           auto-grow
         ></v-textarea>
       </v-col>
       <v-col v-if="sQuestionDetails.items.length >= 1">
         <v-tooltip bottom>
           <template v-slot:activator="{ on }">
             <v-icon
               :data-cy="`Delete${index + 1}`"
               small
               class="ma-1 action-button"
               v-on="on"
               @click="removeItem(index)"
               color="red"
               >close</v-icon
             >
           </template>
           <span>Remove Item</span>
         </v-tooltip>
       </v-col>
     </v-row>

     <v-row>
       <v-btn
         class="ma-auto"
         color="blue darken-1"
         @click="addItem"
         data-cy="addItemItemCombination"
         >Add Item</v-btn
       >
     </v-row>

     <v-row>
       <v-col></v-col>
     </v-row>

     <v-row
       v-for="(itemOne, items) in sQuestionDetails.items"
       :key="items"
     >
       <v-col>
         Chose items to connect with item {{ items + 1 }}
       </v-col>
       <v-col
         v-for="(itemTwo, connect) in sQuestionDetails.items"
         :key="connect"
         v-if="items != connect"
       >
         <v-btn-toggle>
           <v-btn
             outlined
             elevation="10"
             class="ma-auto"
             color="green"
             @click="addConnections(itemOne, itemTwo, items, connect)"
             data-cy="addConnectionsItem"
             >Connect {{ items + 1 }} with {{ connect + 1}}</v-btn
           >
         </v-btn-toggle>
       </v-col>
     </v-row>

   </div>
 </template>

 <script lang="ts">
 import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
 import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
 import Item from '@/models/management/Item';
 import Association from '@/models/management/Association';

 @Component
 export default class ItemCombinationCreate extends Vue {
   @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
   sQuestionDetails!: ItemCombinationQuestionDetails;

   addItem() {
     this.sQuestionDetails.items.push(new Item());
     for (let item of this.sQuestionDetails.items) {
       item.connections.push(new Association());
     }
   }

   removeItem(index: number) {
     this.sQuestionDetails.items.splice(index, 1);
     for (let item of this.sQuestionDetails.items) {
       item.connections.splice(index, 1);
     }
   }

   addConnections(itemOne: Item, itemTwo: Item, index: number, connect: number) {
     var i: number
     i = 0;
     for (let association of itemOne.connections) {
       if (i+1 == connect && index <= i) {
         association.itemTwo = itemTwo.content;
         break;
       }
       if (i == connect && index > i) {
         association.itemTwo = itemTwo.content;
         break;
       }
       i++;
     }
   }

 }
 </script>