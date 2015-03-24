<dal name="SecUserProfileDB_W">
	<databaseSets>
		<databaseSet name="CardRiskDB_INSERT_1" provider="sqlProvider">
            <add name="CardRiskDB_INSERT_1" databaseType="Master" sharding="" connectionString="CardRiskDB_INSERT_1{$DBDataCenter}"/>   
		</databaseSet>
		<databaseSet name="SecUserProfileDB_W" provider="mySqlProvider">
            <add name="SecUserProfileDB_W" databaseType="Master" sharding="" connectionString="SecUserProfileDB_W{$DBDataCenter}"/>   
		</databaseSet>
	</databaseSets>
</dal>