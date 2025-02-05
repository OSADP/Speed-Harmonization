USE [SpeedHarm22]
GO
/****** Object:  Table [dbo].[algorithm]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[algorithm](
	[algo_id] [bigint] IDENTITY(1,1) NOT NULL,
	[class_name] [text] NOT NULL,
	[version_id] [text] NOT NULL,
	[start_time] [datetime] NOT NULL,
	[end_time] [datetime] NULL,
 CONSTRAINT [PK_algorithm] PRIMARY KEY CLUSTERED 
(
	[algo_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[Experiments]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Experiments](
	[exp_id] [bigint] IDENTITY(1,1) NOT NULL,
	[description] [text] NULL,
	[location] [text] NOT NULL,
	[start_time] [datetime] NULL,
	[end_time] [datetime] NULL,
 CONSTRAINT [PK_Experiments] PRIMARY KEY CLUSTERED 
(
	[exp_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[tblRtmsDataMain]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblRtmsDataMain](
	[RTMS_NETWORK_ID] [int] NOT NULL,
	[RTMS_NAME] [char](40) NOT NULL,
	[Zone] [int] NOT NULL,
	[ZoneLabel] [char](40) NULL,
	[Station_Name] [char](40) NULL,
	[Speed] [int] NULL,
	[FWDLK_Speed] [int] NULL,
	[Volume] [int] NULL,
	[Vol_Mid] [int] NULL,
	[Vol_Long] [int] NULL,
	[Vol_Extra_Long] [int] NULL,
	[Occupancy] [float] NULL,
	[MsgNumber] [int] NULL,
	[DateTimeStamp] [datetime] NULL,
	[SensorErrRate] [float] NULL,
	[HealthByte] [int] NULL,
	[SpeedUnits] [bit] NULL,
	[Vol_Mid2] [int] NULL,
	[Vol_Long2] [int] NULL,
	[ID] [int] IDENTITY(1,1) NOT NULL,
 CONSTRAINT [PK__tblRtmsD__9E79E0D60D866D32] PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblRtmsHistory]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblRtmsHistory](
	[DateTimeStamp] [datetime] NOT NULL,
	[RTMS_NETWORK_ID] [int] NOT NULL,
	[RTMS_NAME] [char](40) NOT NULL,
	[Zone] [int] NOT NULL,
	[Speed] [int] NULL,
	[FWDLK_Speed] [int] NULL,
	[Volume] [int] NULL,
	[Vol_Mid] [int] NULL,
	[Vol_Long] [int] NULL,
	[Vol_Extra_Long] [int] NULL,
	[Occupancy] [float] NULL,
	[MsgNumber] [int] NULL,
	[SensorErrRate] [float] NULL,
	[HealthByte] [int] NULL,
	[SpeedUnits] [bit] NULL,
	[Vol_Mid2] [int] NULL,
	[Vol_Long2] [int] NULL,
	[ID] [int] IDENTITY(1,1) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[tblRtmsSetup]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[tblRtmsSetup](
	[RTMS_NETWORK_ID] [int] NOT NULL,
	[RTMS_NAME] [char](40) NOT NULL,
	[HW_ID] [int] NULL,
	[Connection_Type] [int] NULL,
	[IP_Addr] [char](40) NULL,
	[Port_Num] [int] NULL,
	[Cluster_Name] [char](40) NULL,
	[WaitForDataTO] [int] NULL,
	[Time_Corr] [int] NULL,
	[Binary_1] [binary](120) NULL,
	[Binary_2] [binary](120) NULL,
	[Binary_3] [binary](120) NULL,
	[Binary_4] [binary](120) NULL,
	[Binary_5] [binary](120) NULL,
	[Binary_6] [binary](120) NULL,
	[Binary_7] [binary](120) NULL,
	[Setup_Changed] [bit] NULL,
PRIMARY KEY CLUSTERED 
(
	[RTMS_NETWORK_ID] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[VehicleSessions]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [dbo].[VehicleSessions](
	[veh_sess_id] [bigint] IDENTITY(1,1) NOT NULL,
	[uniq_veh_id] [varchar](250) NULL,
	[description] [varchar](250) NULL,
	[registered_at] [datetime] NOT NULL,
	[unregistered_at] [datetime] NULL,
	[algo_id] [bigint] NULL,
	[exp_id] [bigint] NULL
) ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [dbo].[VehicleSpeedCommands]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VehicleSpeedCommands](
	[vsc_id] [bigint] IDENTITY(1,1) NOT NULL,
	[speed] [float] NOT NULL,
	[veh_sess_id] [bigint] NOT NULL,
	[timestamp] [datetime] NOT NULL,
	[command_confidence] [float] NULL,
 CONSTRAINT [PK_VehicleSpeedCommand] PRIMARY KEY CLUSTERED 
(
	[vsc_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[VehicleStatusUpdates]    Script Date: 7/1/2016 4:26:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VehicleStatusUpdates](
	[vsu_id] [bigint] IDENTITY(1,1) NOT NULL,
	[speed] [float] NOT NULL,
	[lat] [float] NOT NULL,
	[lon] [float] NOT NULL,
	[heading] [float] NULL,
	[distance_2_nearest_radar_object] [float] NULL,
	[relative_speed_of_nearest_radar_target] [float] NULL,
	[acceleration] [float] NOT NULL,
	[automated_control_status] [int] NOT NULL,
	[veh_sess_id] [bigint] NOT NULL,
	[vehicle_tx_timestamp] [datetime] NOT NULL,
	[server_rx_timestamp] [datetime] NOT NULL,
	[corrected_tx_timestamp] [datetime] NULL,
	[measured_latency] [int] NULL,
 CONSTRAINT [PK_VehicleStatusUpdates] PRIMARY KEY CLUSTERED 
(
	[vsu_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
ALTER TABLE [dbo].[VehicleSessions]  WITH CHECK ADD  CONSTRAINT [FK_VehicleSessions_algorithm] FOREIGN KEY([algo_id])
REFERENCES [dbo].[algorithm] ([algo_id])
GO
ALTER TABLE [dbo].[VehicleSessions] CHECK CONSTRAINT [FK_VehicleSessions_algorithm]
GO
ALTER TABLE [dbo].[VehicleSessions]  WITH CHECK ADD  CONSTRAINT [FK_VehicleSessions_Experiments] FOREIGN KEY([exp_id])
REFERENCES [dbo].[Experiments] ([exp_id])
GO
ALTER TABLE [dbo].[VehicleSessions] CHECK CONSTRAINT [FK_VehicleSessions_Experiments]
GO
